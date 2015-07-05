#!/bin/bash
unalias -a
export GWT_HOME=/opt/java/lib/gwt-2.7.0
cp src/hibernate.cfg.xml.jndi war/WEB-INF/classes/hibernate.cfg.xml
ant
cp src/hibernate.cfg.xml ./war/WEB-INF/classes/hibernate.cfg.xml
