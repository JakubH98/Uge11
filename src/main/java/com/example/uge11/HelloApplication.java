package com.example.uge11;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HelloApplication extends Application {

    Scanner scanner = null;
    List<BucketID> buckets = new ArrayList<>();
    List<XYChart.Series> listOfSeries = new ArrayList<>();
    File directory = new File("C:/temp/W11_Trashcan");
    final ComboBox month = new ComboBox();
    final ComboBox timeRange = new ComboBox();
    final ComboBox location = new ComboBox();
    final NumberAxis yAxis = new NumberAxis();
    final CategoryAxis xAxis = new CategoryAxis();
    //creating the chart
    final LineChart<String,Number> lineChart =
            new LineChart<String,Number>(xAxis,yAxis);
    @Override public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("Line Chart Sample");

        BorderPane root = new BorderPane();

        month.setDisable(true);
        month.setVisible(false);
        month.getItems().addAll(
                "Januar",
                "Februar",
                "Marts",
                "April",
                "Maj",
                "Juni",
                "Juli",
                "August",
                "September",
                "Oktober",
                "November",
                "December"
        );
        month.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                switchMonth(month.getSelectionModel().getSelectedIndex()+1);
            }
        });


        timeRange.getItems().addAll(
                "Year",
                "Month"
        );
        timeRange.getSelectionModel().select(0);
        timeRange.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                System.out.println(t1.toString());
                if (timeRange.getValue().equals("Month")) {
                    month.setDisable(false);
                    month.setVisible(true);
                    month.getSelectionModel().selectFirst();
                    switchMonth(month.getSelectionModel().getSelectedIndex()+1);
                }else {
                    month.setDisable(true);
                    month.setVisible(false);
                    month.getSelectionModel().selectFirst();
                    populateGraph();
                }
            }
        });

        location.getItems().add("All locations");
        for (File file : directory.listFiles()) {
            location.getItems().add((file.getName().split("[.]"))[0].replace('_',' '));
        }
        location.getSelectionModel().select(0);
        location.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                if (location.getValue().equals("All locations")) {
                    populateGraph();
                }else if (month.getSelectionModel().getSelectedItem() != null) {
                    switchMonth(month.getSelectionModel().getSelectedIndex()+1);
                }else {
                    populateGraph(location.getValue().toString());
                }
            }
        });



        HBox hBox = new HBox();
        hBox.getChildren().addAll(location, timeRange, month);

        root.topProperty().set(hBox);

        //defining the axes
//        final NumberAxis yAxis = new NumberAxis();
//        final CategoryAxis xAxis = new CategoryAxis();
//        //creating the chart
//        final LineChart<String,Number> lineChart =
//                new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle("Food waste, 2024");
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(250);
        //xAxis.setEndMargin(0.5);


        //File directory = new File("C:/temp/W11_Trashcan");

        for (File file : directory.listFiles()) {
            buckets.add(new BucketID(file));
            readContent(file);
            listOfSeries.add(new XYChart.Series());
            populateGraph();
        }

        //Scene scene  = new Scene(lineChart,1400,600);
        Scene scene = new Scene(root,1400,600);

        root.setCenter(lineChart);

        stage.setScene(scene);
        stage.show();
    }

    private void switchMonth(int selectedMonth) {
        if (location.getValue().equals("All locations")) {
            populateGraph(selectedMonth);
        }else {
            populateGraph(selectedMonth,location.getValue().toString());
        }
    }

    private void populateGraph() {
        listOfSeries.clear();
        lineChart.getData().clear();
        for(int j = 0; j < buckets.size(); j++) {
            listOfSeries.add(new XYChart.Series());
            int tempLowerBound = 90001;
            int tempUpperBound = 0;
            for (int i = 1; i <= 12; i++) {
                int tempTotal = 0;
                for (Record s : buckets.get(j).getData()) {
                    if (s.month == i) {
                        tempTotal += Integer.parseInt(s.weight);
                    }
                }
                System.out.println("Month: " + i + " total weight: " + tempTotal);
                listOfSeries.get(j).getData().add(new XYChart.Data(new DateFormatSymbols().getMonths()[i-1], tempTotal));
                if(tempTotal < tempLowerBound){
                    tempLowerBound = tempTotal;
                    yAxis.setLowerBound(tempTotal-500);
                }
                if(tempTotal > tempUpperBound){
                    tempUpperBound = tempTotal;
                    yAxis.setUpperBound(tempTotal+500);
                }
            }
            listOfSeries.get(j).setName((buckets.get(j).getFileName().split("[.]"))[0]);
        }

        for(XYChart.Series s : listOfSeries)
        {
            lineChart.getData().add(s);
        }

    }
    private void populateGraph(int selectedMonth) {
        listOfSeries.clear();
        lineChart.getData().clear();
        //lineChart.getData().clear();
        for(int j = 0; j < buckets.size(); j++) {
            listOfSeries.add(new XYChart.Series());
            int tempLowerBound = 90001;
            int tempUpperBound = 0;
            for (int i = 1; i <= 31; i++) {
                int tempTotal = 0;
                for (Record s : buckets.get(j).getData()) {
                    if (s.month == selectedMonth && s.day == i) {
                        tempTotal += Integer.parseInt(s.weight);
                    }
                }
                System.out.println("Month: " + i + " total weight: " + tempTotal);
                listOfSeries.get(j).getData().add(new XYChart.Data(String.valueOf(i), tempTotal));
                if(tempTotal < tempLowerBound){
                    tempLowerBound = tempTotal;
                    yAxis.setLowerBound(tempTotal-500);
                }
                if(tempTotal > tempUpperBound){
                    tempUpperBound = tempTotal;
                    yAxis.setUpperBound(tempTotal+500);
                }
            }
            listOfSeries.get(j).setName((buckets.get(j).getFileName().split("[.]"))[0]);
        }
        for(XYChart.Series s : listOfSeries)
        {
            lineChart.getData().add(s);
        }


    }
    private void populateGraph(String selectedLocation) {
        listOfSeries.clear();
        lineChart.getData().clear();
        //lineChart.getData().clear();
        for(int j = 0; j < buckets.size(); j++) {
            int tempLowerBound = 90001;
            int tempUpperBound = 0;
            if (buckets.get(j).getData().get(0).adress.equals(selectedLocation)) {
                listOfSeries.add(new XYChart.Series());
                for (int i = 1; i <= 12; i++) {
                    int tempTotal = 0;
                    for (Record s : buckets.get(j).getData()) {
                        if (s.day == i) {
                            tempTotal += Integer.parseInt(s.weight);
                        }
                    }
                    System.out.println("Month: " + i + " total weight: " + tempTotal);
                    listOfSeries.get(0).getData().add(new XYChart.Data(new DateFormatSymbols().getMonths()[i-1], tempTotal));
                    if (tempTotal < tempLowerBound) {
                        tempLowerBound = tempTotal;
                        yAxis.setLowerBound(tempTotal - 500);
                    }
                    if (tempTotal > tempUpperBound) {
                        tempUpperBound = tempTotal;
                        yAxis.setUpperBound(tempTotal + 500);
                    }
                    listOfSeries.get(0).setName((buckets.get(j).getFileName().split("[.]"))[0]);
                }
            }
        }
        for(XYChart.Series s : listOfSeries)
        {
            lineChart.getData().add(s);
        }
    }
    private void populateGraph(int selectedMonth,String selectedLocation) {
        listOfSeries.clear();
        lineChart.getData().clear();
        //lineChart.getData().clear();
        for(int j = 0; j < buckets.size(); j++) {
            int tempLowerBound = 90001;
            int tempUpperBound = 0;
            if (buckets.get(j).getData().get(0).adress.equals(selectedLocation)) {
                listOfSeries.add(new XYChart.Series());
                for (int i = 1; i <= 31; i++) {
                    int tempTotal = 0;
                    for (Record s : buckets.get(j).getData()) {
                        if (s.month == selectedMonth && s.day == i) {
                            tempTotal += Integer.parseInt(s.weight);
                        }
                    }
                    System.out.println("Month: " + i + " total weight: " + tempTotal);
                    listOfSeries.get(0).getData().add(new XYChart.Data(String.valueOf(i), tempTotal));
                    if (tempTotal < tempLowerBound) {
                        tempLowerBound = tempTotal;
                        yAxis.setLowerBound(tempTotal - 500);
                    }
                    if (tempTotal > tempUpperBound) {
                        tempUpperBound = tempTotal;
                        yAxis.setUpperBound(tempTotal + 500);
                    }
                    listOfSeries.get(0).setName((buckets.get(j).getFileName().split("[.]"))[0]);
                }
            }
        }
        for(XYChart.Series s : listOfSeries)
        {
            lineChart.getData().add(s);
        }
    }

    private void readContent(File directory) {
        try  {
            scanner = new Scanner(directory);
            if(scanner.hasNextLine())
            {
                scanner.nextLine();
            }
            while(scanner.hasNextLine())
            {

                String line = scanner.nextLine();
                String[] tempArray = line.split("[:,]");
                for (int i = 0; i < tempArray[0].length(); i++) {
                    System.out.println(tempArray[8]);
                }
                buckets.get(buckets.size()-1).setData(new Record(tempArray[0],
                        tempArray[1],
                        Integer.parseInt(tempArray[2]),
                        tempArray[3],
                        Integer.parseInt(tempArray[4]),
                        Integer.parseInt(tempArray[5]),
                        Integer.parseInt(tempArray[6]),
                        Integer.parseInt(tempArray[7]),
                        Integer.parseInt(tempArray[8]))
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}