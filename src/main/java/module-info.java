module org.donel.taskmanagerdesktop {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.net.http;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;

    opens org.donel.taskmanagerdesktop to javafx.fxml;
    opens org.donel.taskmanagerdesktop.Controllers to javafx.fxml;

    exports org.donel.taskmanagerdesktop;
    exports org.donel.taskmanagerdesktop.Controllers;
    exports org.donel.taskmanagerdesktop.api;
    exports org.donel.taskmanagerdesktop.services;
    exports org.donel.taskmanagerdesktop.Dialogs;
    exports org.donel.taskmanagerdesktop.database;
    opens org.donel.taskmanagerdesktop.Dialogs to javafx.fxml;
}