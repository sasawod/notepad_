# Notepad (Java Swing) — Maven project

Это простой клон блокнота (Notepad) на Java (Swing) с поддержкой:
- новый файл / открыть / сохранить / сохранить как
- копировать/вставить/вырезать/выделить всё
- простое окно "О программе"

## Требования
- Java 11+
- Maven 3.6+

## Сборка
В каталоге проекта выполните:
```bash
mvn clean package
```
После сборки в папке `target` появится `notepad-1.0-jar-with-dependencies.jar` — исполняемый JAR.

## Запуск
```bash
java -jar target/notepad-1.0-jar-with-dependencies.jar
```

## Файлы
- `src/main/java/com/example/notepad/Notepad.java` — основной код приложения.
- `pom.xml` — Maven конфигурация.

Если нужно, могу дополнительно собрать JAR и выслать уже собранный исполняемый архив. 
