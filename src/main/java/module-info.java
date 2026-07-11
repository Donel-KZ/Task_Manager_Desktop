module org.donel.taskmanagerdesktop {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;

    opens org.donel.taskmanagerdesktop to javafx.fxml;
    opens org.donel.taskmanagerdesktop.controllers to javafx.fxml;

    exports org.donel.taskmanagerdesktop;
    exports org.donel.taskmanagerdesktop.controllers;
}