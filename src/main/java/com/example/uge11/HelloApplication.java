package com.example.uge11;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

    @Override public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("Line Chart Sample");
        //defining the axes
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        //creating the chart
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(250);
        xAxis.setEndMargin(0.5);

        File directory = new File("C:/test/memeTest");

        for (File file : directory.listFiles()) {
            buckets.add(new BucketID(file));
            readContent(file);
            listOfSeries.add(new XYChart.Series());
        }

        for(int j = 0; j < buckets.size(); j++) {
            int tempLowerBound = 90001;
            int tempUpperBound = 0;
            for (int i = 1; i <= 12; i++) {
                int tempTotal = 0;
                int totelWeight = 0;
                for (Record s : buckets.get(j).getData()) {
                    if (s.month == i) {
                        if(tempTotal >= Integer.parseInt(s.weight)){ //Remove '=' if trash is not emptied if the same value
                            totelWeight += tempTotal;
                            tempTotal = Integer.parseInt(s.weight);
                        }
                        else{
                            tempTotal = Integer.parseInt(s.weight);
                        }
                        //tempTotal += Integer.parseInt(s.weight);
                    }
                }
                System.out.println("Month: " + i + " total weight: " + totelWeight);
                listOfSeries.get(j).getData().add(new XYChart.Data(new DateFormatSymbols().getMonths()[i-1], totelWeight));
                if(tempTotal < tempLowerBound){
                    tempLowerBound = tempTotal;
                    yAxis.setLowerBound(totelWeight-500);
                }
                if(tempTotal > tempUpperBound){
                    tempUpperBound = tempTotal;
                    yAxis.setUpperBound(totelWeight+500);
                }
            }
            listOfSeries.get(j).setName((buckets.get(j).getFileName().split("[.]"))[0]);
        }

        Scene scene  = new Scene(lineChart,1400,600);
        for(XYChart.Series s : listOfSeries)
        {
            lineChart.getData().add(s);

        }

        stage.setScene(scene);
        stage.show();
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