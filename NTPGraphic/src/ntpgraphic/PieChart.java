/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntpgraphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Rotation;

/**
 *
 * @author Giancarlo
 */
public class PieChart extends JFrame{
 
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
    
    public static DefaultPieDataset pieDataset = new DefaultPieDataset();
    public static int offset1 = 0;
    public static int offset2 = 0;
    public static int offset3 = 0;
    
        static JFreeChart jfreechart;

    
	public PieChart(String s) throws IOException
	{
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 320));
		setContentPane(jpanel);
                ChartUtilities.saveChartAsPNG(new File("/Users/JuanCa/Desktop/grafica_9.png"), jfreechart, 500, 300);
	}

 	public static JPanel createDemoPanel()
	{
		jfreechart = createChart(createDataset());
		return new ChartPanel(jfreechart);
	}
        
        private static JFreeChart createChart(PieDataset piedataset)
	{
		jfreechart = ChartFactory.createPieChart3D("Pie Chart 3D Demo 1", piedataset, true, true, false);
		PiePlot3D pieplot3d = (PiePlot3D)jfreechart.getPlot();
		pieplot3d.setDarkerSides(true);
		pieplot3d.setStartAngle(36 * 9);
		pieplot3d.setNoDataMessage("No data to display");
		return jfreechart;
	}

        public static void main(String args[]) throws IOException
	{
		PieChart piechart3ddemo1 = new PieChart("JFreeChart: PieChart3DDemo1.java");
		piechart3ddemo1.pack();
		RefineryUtilities.centerFrameOnScreen(piechart3ddemo1);
		piechart3ddemo1.setVisible(true);
	}

        
    private static PieDataset createDataset()
    {
        pieDataset = new DefaultPieDataset();
        
        String[] servers = {serv1, serv2, serv3};
        NTPClient(servers);

        pieDataset.setValue(serv1, offset1);
        pieDataset.setValue(serv2, offset2);
        pieDataset.setValue(serv3, offset3);
        
        return pieDataset;
    }
    
    public static void NTPClient(String[] servers){
    
        Properties defaultProps = System.getProperties(); //obtiene las "properties" del sistema
        defaultProps.put("java.net.preferIPv6Addresses", "true");//mapea el valor true en la variable java.net.preferIPv6Addresses
        if (servers.length == 0) {
            System.err.println("Usage: NTPClient <hostname-or-address-list>");
            System.exit(1);
        }

        Promedio=0;
        Cant=0;
        int j = 1;
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
                    processResponse(info,j++);
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
        switch(i){
            case 1:
                offset1 = (int) (long) offsetValue;
                break;
            case 2:
                offset2 = (int) (long) offsetValue;
                break;
            case 3:
                offset3 = (int) (long) offsetValue;
                break;
            case 4:
                series4.add(i, delayValue);
                break;
        }
        
        System.out.println(" Roundtrip delay(ms)=" + delay  + ", clock offset(ms)=" + offset); // offset in ms
        //Promedio=Promedio+offsetValue;
        //Cant=Cant+1;
    }

}
