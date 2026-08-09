package io.github.arseniji.kalah.desktop;

import javafx.application.Application;

/**
 * Отдельная точка входа: класс-запускатель НЕ наследует Application.
 * Если main лежит прямо в наследнике Application, запуск с обычного classpath
 * падает с «JavaFX runtime components are missing» — модульный путь тут не настроен.
 */
public class DesktopApp {
    public static void main(String[] args) {
        Application.launch(KalahApp.class, args);
    }
}
