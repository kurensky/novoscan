package ru.novaris.novoscan.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

import org.geotools.util.Converters;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.micromata.opengis.kml.v_2_2_0.ExtendedData;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.gx.SimpleArrayData;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;
import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.domain.DataSensor;
import ru.novaris.novoscan.util.HibernateUtil;

/**
 * 
 * @author E.A. Kurensky
 */
public class ReportServlet extends HttpServlet implements ImplConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String baseFileName;

	private Session session;

	private Connection connection;

	private String reportFileExtensions;

	private String reportFileName;

	private Placemark pm;

	private Track track;

	private Long timeOffset = 0L;

	private String realFileName;

	private Long uid;

	private Date dateBeg;

	private Date dateEnd;
	
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private String fullFileName;

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			JAVA_DATE_FORMAT);

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServlet.class);

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		FileInputStream fileInputStream;
		BufferedInputStream bufferedInputStream;
		try {
			// set header as text
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, String[]> servletParams = request.getParameterMap();
			// get report location
			ServletContext context = getServletContext();
			final String reportLocation = context.getRealPath("WEB-INF")
					+ REPORT_FILE_DIR;
			final String tempLocation = context.getRealPath("/")
					+ TEMP_FILE_DIR;
			Set<?> entrySet = servletParams.entrySet();
			Iterator<?> it = entrySet.iterator();
			while (it.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<String, String[]> entry = (Entry<String, String[]>) it
						.next();
				String paramName = entry.getKey();
				String[] paramValues = entry.getValue();
				Locale locale = new Locale("ru", "RU");
				params.put(JRParameter.REPORT_LOCALE, locale);
				params.put("SUBREPORT_DIR", reportLocation);
				if (paramValues.length >= 1) {
					if (paramName.equalsIgnoreCase(REPORT_FILE)) {
						baseFileName = paramValues[0];
					} else if (paramName.equalsIgnoreCase(FORMAT_FILE)) {
						reportFileExtensions = paramValues[0];
					} else if (paramName
							.equalsIgnoreCase(COOKIE_TIMEZONE_OFFSET)) {
						params.put(paramName, Integer.valueOf(paramValues[0]));
						// millisecond
						timeOffset = Long.valueOf(paramValues[0]) * 60000;
					} else {
						params.put(paramName, paramValues[0]);
					}
				}

			}
			final StringBuffer tempData = new StringBuffer();
			final String fileName = UUID.randomUUID().toString();
			fullFileName = new StringBuffer().append(baseFileName)
					.append("_").append(fileName).append(".")
					.append(reportFileExtensions).toString();

			realFileName = tempData.append(tempLocation).append(fullFileName)
					.toString();
			tempData.setLength(0);
			reportFileName = tempData.append(TEMP_FILE_DIR)
					.append(fullFileName).toString();
			tempData.setLength(0);
			uid = Long.valueOf((String) params.get("spmd_uid"));
			session = HibernateUtil.getSessionFactory().openSession();
			SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session
					.getSessionFactory();
			if (reportFileExtensions.equalsIgnoreCase("KML")
					|| reportFileExtensions.equalsIgnoreCase("KMZ")
					|| reportFileExtensions.equalsIgnoreCase("GPX")) {
				dateBeg = formatter.parse((String) params.get("date_beg"));
				dateEnd = formatter.parse((String) params.get("date_end"));
				int offset = (Integer) params.get(COOKIE_TIMEZONE_OFFSET);
				getDataSensor(uid, dateBeg, dateEnd, offset);
			} else {
				fileInputStream = new FileInputStream(tempData
						.append(reportLocation).append(baseFileName)
						.append(JASPER_FILE_EXTENSION).toString());
				bufferedInputStream = new BufferedInputStream(fileInputStream);
				JasperReport jasperReport = (JasperReport) JRLoader
						.loadObject(bufferedInputStream);
				fileInputStream.close();
				bufferedInputStream.close();

				ConnectionProvider connectionProvider = sessionFactoryImplementation
						.getConnectionProvider();
				connection = connectionProvider.getConnection();
				connection.setAutoCommit(true);
				JasperPrint jasperPrint = JasperFillManager.fillReport(
						jasperReport, params, connection);
				if (reportFileExtensions.equalsIgnoreCase("XLS")) {
					JRXlsExporter xlsExporter = new JRXlsExporter();
					xlsExporter.setExporterInput(new SimpleExporterInput(
							jasperPrint));
					xlsExporter
							.setExporterOutput(new SimpleOutputStreamExporterOutput(
									realFileName));
					SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
					configuration.setOnePagePerSheet(true);
					configuration.setDetectCellType(true);
					configuration.setCollapseRowSpan(false);
					xlsExporter.setConfiguration(configuration);
					xlsExporter.exportReport();
				} else {
					JasperExportManager.exportReportToPdfFile(jasperPrint,
							realFileName);
				}
				connection.close();
			}
			session.close();
			response.setContentType("text/plain");
			response.getOutputStream().print(reportFileName);
			// ToDo удалить файл
		} catch (Exception e) {
			logger.error("Ошибка исполнения отчёта \"" + reportFileName
					+ "\" : " + e.getStackTrace().toString());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.setContentType("text/plain");
			closeAll();
		}

	}

	@SuppressWarnings("unchecked")
	private void getDataSensor(long dasnUid, Date dateBeg, Date dateEnd,
			int offset) throws Exception {
		// TODO Auto-generated method stub
		session.beginTransaction();
		List<DataSensor> sqlResult = new ArrayList<DataSensor>();
		Date dt1 = new Date(dateBeg.getTime() + timeOffset);
		Date dt2 = new Date(dateEnd.getTime() + timeOffset);
		Criteria sqlTemp = session.createCriteria(DataSensor.class);
		sqlTemp.add(Restrictions.eq("dasnUid", dasnUid));
		sqlTemp.add(Restrictions.ge("dasnDatetime", dt1));
		sqlTemp.add(Restrictions.le("dasnDatetime", dt2));
		sqlTemp.add(Restrictions.and(
				Restrictions.ne("dasnLatitude", (double) 0),
				Restrictions.ne("dasnLongitude", (double) 0)));
		sqlTemp.addOrder(Order.asc("dasnDatetime"));
		sqlTemp.addOrder(Order.asc("dasnId"));
		sqlResult = new ArrayList<DataSensor>(sqlTemp.list());
		session.getTransaction().commit();
		if (reportFileExtensions.equalsIgnoreCase("KML")
				|| reportFileExtensions.equalsIgnoreCase("KMZ")) {
			createKmlFile(sqlResult);
		} else if (reportFileExtensions.equalsIgnoreCase("GPX")) {
			createGpxFile(sqlResult);
		}

	}

	private void createGpxFile(List<DataSensor> sqlResult) throws Exception {
		long id = 0L;
		if (sqlResult.size() > 0) {
			id = sqlResult.get(0).getDasnId();
		}
		File report = new File(realFileName);
		PrintWriter pw = new PrintWriter(report);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
				.append("<?xml-stylesheet type=\"text/xsl\" href=\"details.xsl\"?>\n")
				.append("<gpx version=\"1.1\" creator=\"Novoscan Track System 1.1.0\" ")
				.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
				.append("xmlns=\"http://www.topografix.com/GPX/1/1\" ")
				.append("xmlns:topografix=\"http://www.topografix.com/GPX/Private/TopoGrafix/0/1\" ")
				.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 ")
				.append("http://www.topografix.com/GPX/1/1/gpx.xsd ")
				.append("http://www.topografix.com/GPX/Private/TopoGrafix/0/1 ")
				.append("http://www.topografix.com/GPX/Private/TopoGrafix/0/1/topografix.xsd\">\n");
		write(pw, stringBuffer);

		stringBuffer
				.append("<trk>\n")
				.append("<name>Трэк объекта : ")
				.append(Converters.convert(uid, String.class))
				.append("</name>")
				.append("<desc>Трэк объекта ")
				.append(Converters.convert(uid, String.class))
				.append(" системы Новоскан  за интервал дат ")
				.append(dateBeg)
				.append(" - ")
				.append(dateEnd)
				.append("</desc>")
				.append("<number>")
				.append(id)
				.append("</number>")
				.append("<extensions><topografix:color>c0c0c0</topografix:color></extensions>\n")
				.append("<trkseg>\n");
		write(pw, stringBuffer);
		for (DataSensor ds : sqlResult) {
			stringBuffer
					.append("<trkpt lat=\"")
					.append(Converters.convert(ds.getDasnLatitude(),
							String.class))
					.append("\" lon=\"")
					.append(Converters.convert(ds.getDasnLongitude(),
							String.class))
					.append("\">")
					.append("<ele>")
					.append(Converters.convert(ds.getDasnHgeo(), String.class))
					.append("</ele>")
					.append("<time>")
					.append(df.format(ds.getDasnDatetime()))
					.append("</time>")
					.append("<sat>")
					.append(Converters.convert(ds.getDasnSatUsed(),
							String.class))
					.append("</sat>")
					.append("<course>")
					.append(Converters.convert(ds.getDasnCourse(), String.class))
					.append("</course>")
					.append("<speed>")
					.append(Converters.convert((ds.getDasnSog() / 3.6),
							String.class)).append("</speed>")
					.append("</trkpt>\n");
			write(pw, stringBuffer);
		}
		stringBuffer.append("</trkseg>\n").append("</trk>\n")
				.append("</gpx>\n");
		write(pw, stringBuffer);
		pw.close();
		zipFile(report);
	}

	private void zipFile(File report) throws IOException {
		realFileName = realFileName.replace("." + reportFileExtensions, ".zip");
		reportFileName = reportFileName.replace("." + reportFileExtensions, ".zip");
		byte data[] = new byte[BUFFER];
		FileOutputStream zipFile = new 
		           FileOutputStream(realFileName);
		ZipOutputStream zipOutput = new ZipOutputStream(new 
		           BufferedOutputStream(zipFile));
		FileInputStream file = new 
	              FileInputStream(report);
		BufferedInputStream bufferInput = new 
	              BufferedInputStream(file, BUFFER);
		ZipEntry entry = new ZipEntry(fullFileName);
		zipOutput.putNextEntry(entry);
		int count;
        while((count = bufferInput.read(data, 0, 
          BUFFER)) != -1) {
           zipOutput.write(data, 0, count);
        }
        bufferInput.close();
        zipOutput.close();
        report.delete();
	}

	private void write(PrintWriter pw, StringBuffer stringBuffer) {
		pw.print(stringBuffer);
		stringBuffer.setLength(0);
	}

	private void createKmlFile(List<DataSensor> sqlResult) throws Exception {
		Kml kml = KmlFactory.createKml();
		// Create <Placemark> and set values.
		pm = KmlFactory.createPlacemark();
		// List<Coordinate> coordinates

		track = new Track();
		List<String> coord = new ArrayList<String>();
		List<String> when = new ArrayList<String>();
		List<String> angles = new ArrayList<String>();

		List<SchemaData> schemaExt = new ArrayList<SchemaData>();
		ExtendedData ext = new ExtendedData();
		SimpleArrayData simpleArraySpeed = new SimpleArrayData();
		simpleArraySpeed.setName("скорость");
		SimpleArrayData simpleArraySat = new SimpleArrayData();
		simpleArraySat.setName("спутники");

		for (DataSensor ds : sqlResult) {
			coord.add(Converters.convert(
					ds.getDasnLongitude() + " " + ds.getDasnLatitude() + " "
							+ ds.getDasnHgeo(), String.class));
			when.add(df.format(ds.getDasnDatetime()));
			angles.add(Converters.convert(ds.getDasnCourse(), String.class));
			simpleArraySpeed.addToValue(Converters.convert(ds.getDasnSog(),
					String.class));
			simpleArraySat.addToValue(Converters.convert(ds.getDasnSatUsed(),
					String.class));
		}

		SchemaData schemaData = new SchemaData();
		schemaData.addToSchemaDataExtension(simpleArraySat);
		schemaData.addToSchemaDataExtension(simpleArraySpeed);
		schemaExt.add(schemaData);
		ext.setSchemaData(schemaExt);
		track.setWhen(when);
		track.setCoord(coord);
		track.setAngles(angles);
		track.setExtendedData(ext);
		pm.setName("Трэк объекта (ИД) : " + uid);
		pm.setVisibility(true);
		pm.setOpen(false);
		pm.setDescription("Трэк объекта системы Новоскан за интервал дат : "
				+ dateBeg + " - " + dateEnd);
		pm.setGeometry(track);
		kml.setFeature(pm);
		if (reportFileExtensions.equals("KMZ")) {
			kml.marshalAsKmz(realFileName);
		} else {
			File report = new File(realFileName);
			kml.marshal(report);
		}
	}

	protected void finalize() {
		closeAll();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return "Novoscan Reporting Servlet";
	}

	private void closeAll() {
		if (session != null & session.isOpen()) {
			session.close();
		}
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
}
