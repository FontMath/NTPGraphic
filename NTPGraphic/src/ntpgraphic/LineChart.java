/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntpgraphic;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;

/**
 *
 * @author Giancarlo
 */
public class LineChart extends JFrame{
 
    private static final NumberFormat numberFormat = new java.text.DecimalFormat("0.00");
    private static long Promedio;
    private static int Cant;
    public static int serie=1;
    
    public static String serv1 = "192.100.201.200";
    public static String serv2 = "192.100.201.254";
    public static String serv3 = "192.100.201.199";
    public static String serv4 = "172.25.4.100";
    
    public XYSeriesCollection dataset = new XYSeriesCollection();
    public static boolean autoSort = false;
    public static boolean allowDuplicateXValues = false;
    public static XYSeries series1 = new XYSeries(serv1, autoSort, allowDuplicateXValues);
    public static XYSeries series2 = new XYSeries(serv2, autoSort, allowDuplicateXValues);
    public static XYSeries series3 = new XYSeries(serv3, autoSort, allowDuplicateXValues);
    public static XYSeries series4 = new XYSeries(serv4, autoSort, allowDuplicateXValues);
    
    
    
    public LineChart() {
        super("NTP Graphics with JFreechart");
 
        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
 
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
    }
 
    private JPanel createChartPanel() {
        String chartTitle = "NTP Graphic";
        String xAxisLabel = "Time";
        String yAxisLabel = "Roundtrip delay(ms)";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled);

        File imageFile = new File("NTPGraphic.png");
        int width = 640;
        int height = 480;

        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        return new ChartPanel(chart);
    }
    
    
 
    private XYDataset createDataset() {
        /*
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Series Name");

        series1.add(x1, y1);
        series1.add(x2, y2);
        // ...
        series1.add(xN, yN);

        dataset.addSeries(series1);
        */

        for( int i=1;i<=100;i++){
            String[] servers = {serv1,serv2,serv3};
                serie=1;
                NTPClient(servers,i);
        }
        
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        //dataset.addSeries(series4);

        return dataset;
    }
 
    public static void NTPClient(String[] servers,int i){
    
        Properties defaultProps = System.getProperties(); //obtiene las "properties" del sistema
        defaultProps.put("java.net.preferIPv6Addresses", "true");//mapea el valor true en la variable java.net.preferIPv6Addresses
        if (servers.length == 0) {
            System.err.println("Usage: NTPClient <hostname-or-address-list>");
            System.exit(1);
        }

        Promedio=0;
        Cant=0;
        NTPUDPClient client = new NTPUDPClient();
        // We want to timeout if a response takes longer than 10 seconds
        client.setDefaultTimeout(10000);
        try {
            client.open();
            for (String arg : servers)
            {
                System.out.println();
                try {
                    InetAddress hostAddr = InetAddress.getByName(arg);
                    System.out.println("> " + hostAddr.getHostName() + "/" + hostAddr.getHostAddress());
                    TimeInfo info = client.getTime(hostAddr);
                    processResponse(info,i);
                } catch (IOException ioe) {
                    System.err.println(ioe.toString());
                }
            }
        } catch (SocketException e) {
            System.err.println(e.toString());
        }

        client.close();
        //System.out.println("\n Pomedio "+(Promedio/Cant));
    }
    
    public static void processResponse(TimeInfo info,int i)
    {
        info.computeDetails();
        Long offsetValue = info.getOffset();
        Long delayValue = info.getDelay();
        String delay = (delayValue == null) ? "N/A" : delayValue.toString();
        String offset = (offsetValue == null) ? "N/A" : offsetValue.toString();
        if(delayValue == null){delayValue=(long)0;}
        switch(serie){
            case 1:
                series1.add(i, delayValue);
                break;
            case 2:
                series2.add(i, delayValue);
                break;
            case 3:
                series3.add(i, delayValue);
                break;
            case 4:
                series4.add(i, delayValue);
                break;
        }
        serie++;
        
        System.out.println(" Roundtrip delay(ms)=" + delay  + ", clock offset(ms)=" + offset); // offset in ms
        //Promedio=Promedio+offsetValue;
        //Cant=Cant+1;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LineChart().setVisible(true);
            }
        });
    }
}
