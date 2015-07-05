package ru.novaris.novoscan.client;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;





import ru.novaris.novoscan.client.resources.ImplConstants;
//import ru.novaris.novoscan.client.reports.ReportManager;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.SprvReportTypes;
import ru.novaris.novoscan.reports.CpReportSelect;

public class MainMenu extends MenuBar implements ImplConstantsGWT, ImplConstants {

	private final Novoscan entryPoint;

	private final WidgMaps maps;

	private final MenuBar configureMenu = new MenuBar(true);

	private final MenuBar claimMenu = new MenuBar(true);

	private final MenuBar reportMenu = new MenuBar(true);

	private final List<String> listRoles;

	public MainMenu(final Novoscan entryPoint) {
		this.entryPoint = entryPoint;
		this.listRoles = entryPoint.getRoles();
		this.viewMenu();
		RootPanel.get(DIV_MENU).add(this);
		maps = new WidgMaps(entryPoint);
		entryPoint.setNeedReCenter(true);
		entryPoint.setNeedReZoom(true);
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
		this.setMapStack(constants.Maps());
	}

	// ******************
	public void viewMenu() {

		Command mapOpenStreet = new Command() {
			public void execute() {
				cleanCurrentStack();
				setMapStack(constants.Maps());
			}
		};


		configureMenu.addItem(constants.Departs(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmDeparts departs = new CwAdmDeparts(entryPoint);
				entryPoint.setContent(departs);
			}
		});
		configureMenu.addItem(constants.Clients(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmClients clients = new CwAdmClients(entryPoint);
				entryPoint.setContent(clients);
			}
		});
		configureMenu.addItem(constants.Objects(), 	new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmObjects objects = new CwAdmObjects(entryPoint, true);
				entryPoint.setContent(objects);
			}
		});
		configureMenu.addItem(constants.Terminals(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmModules modules = new CwAdmModules(entryPoint);
				entryPoint.setContent(modules);
			}
		});
		configureMenu.addItem(constants.Users(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmAccounts accounts = new CwAdmAccounts(entryPoint);
				entryPoint.setContent(accounts);
			}
		});
		configureMenu.addItem(constants.Roles(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmAccountRoles roles = new CwAdmAccountRoles(entryPoint);
				entryPoint.setContent(roles);
			}
		});
		configureMenu.addItem(constants.Acls(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmAcl acl = new CwAdmAcl(entryPoint);
				entryPoint.setContent(acl);
			}
		});
		configureMenu.addItem(constants.EventLog(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwAdmEventLog eventlog = new CwAdmEventLog(entryPoint);
				entryPoint.setContent(eventlog);
			}
		});


		claimMenu.addItem(constants.MyClaims(), new Command() {
			public void execute() {
				cleanCurrentStack();
			}
		});


		// GWT.log("Загружены роли: " + entryPoint.getRoles());
		if (listRoles.contains("DEVELOPER")
				|| listRoles.contains("I01_ADM_ADMINISTRATOR")
				|| listRoles.contains("I01_OPER_FULL")) {
			// GWT.log("Роль Оператора...");
			this.addItem(constants.Monitoring(), mapOpenStreet);
			if (listRoles.contains("DEVELOPER")
					|| listRoles.contains("I01_ADM_ADMINISTRATOR")) {
				// GWT.log("Роль Админа...");
				this.addItem(constants.Configure(), configureMenu);
			}
			this.addItem(constants.MyObjects(), new Command() {
				public void execute() {
					cleanCurrentStack();
					CwAdmObjects objects = new CwAdmObjects(entryPoint);
					entryPoint.setContent(objects);
				}
			});
		}
		if (listRoles.contains("DEVELOPER")
				|| listRoles.contains("I01_ADM_ADMINISTRATOR")
				|| listRoles.contains("I01_OPER_FULL")
				|| listRoles.contains("I01_CLIENT_FULL")) {

			// GWT.log("Роль Клиента...");
			this.addItem(constants.Claims(), claimMenu);
			// this.addItem(constants.Find(), search);
		}

		this.addItem(constants.Reports(), reportMenu);

		// // Таблица отчётов и параметров!!!!

		AsyncCallback<List<SprvReportTypes>> callback = new AsyncCallback<List<SprvReportTypes>>() {

			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, caught.getMessage());
			}

			@Override
			public void onSuccess(List<SprvReportTypes> reportTypes) {
				for (SprvReportTypes reportType : reportTypes) {
					final long sprtId = reportType.getSprtId();
					final String sprtName = reportType.getSprtName();
					reportMenu.addItem(reportType.getSprtName(), new Command() {
						public void execute() {
							CpReportSelect object = new CpReportSelect(
									entryPoint, sprtId);
							object.setTitle(sprtName);
							object.setPopupPosition(RootPanel.get("Content").getAbsoluteLeft(), RootPanel.get("Content").getAbsoluteTop());
							object.setModal(true);
							object.show();
						}
					});
				}

			}

		};
		rpcObject.getListReportTypes(callback);


		claimMenu.addItem(constants.TownDistance(), new Command() {
			public void execute() {
				cleanCurrentStack();
				CwGisDistance objects = new CwGisDistance(entryPoint);
				entryPoint.setContent(objects);
			}
		});
		this.addSeparator();
		this.addItem(constants.Exit()+" ("+Cookies.getCookie(COOKIE_TAG_NAME)+")", new Command() {
			public void execute() {
				cleanCurrentStack();
				entryPoint.logoff();
			}
		});
		
		this.addSeparator();
	}

	public void setMapStack(String title) {
		//maps.setTitle(title);
		maps.setVisible(true);
		maps.showInfoPanel();
		maps.refreshData();
		entryPoint.setContent(maps);
	}

	private void cleanCurrentStack() {
		if (maps != null) {
			maps.stopRefreshTimer();
			maps.setVisible(false);
			maps.deleteInfoPanel();
		}
	}

	public void refreshMapData() {
		maps.refreshData();
	}

	public void addTrack() {
		maps.addTrack();
	}

	public void clearTrack() {
		maps.clearTrack();
	}

	protected void finalize() {
		cleanCurrentStack();
	}

}
