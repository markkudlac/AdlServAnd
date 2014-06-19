package com.adserv.adladl;

import java.io.File;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;

import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import static com.adserv.adladl.Const.*;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.format.Time;

public class Util {

	 static   void DeleteRecursive(File fileOrDirectory) {
		 
		 System.out.println("IN deleteRecursive : " + fileOrDirectory.getAbsolutePath());
	            if (fileOrDirectory.isDirectory())
	                for (File child : fileOrDirectory.listFiles())
	                    DeleteRecursive(child);

	            fileOrDirectory.delete();
	        }
	 
	 

    public static void versionChangeHTML(Context mnact ) {     	
     	
	        	try {
	        		
	        		File htmlpar,userdir;
	        		      		
	        		userdir = new File(mnact.getFilesDir(),USERHTML_DIR);
	        		htmlpar = new File(mnact.getFilesDir(),HTML_DIR);
	        		
	      //*******  This delete is here for testing now 
	       // 		if (userdir.exists())	DeleteRecursive(userdir); 
	      //*************
	        		
	        		if (!userdir.exists()){
	        			userdir.mkdirs();	//Create the user directory if it doesn't exit
	        		}
	        		
	        		System.out.println("HTML DIR is : "+userdir.getAbsolutePath());
	        		
     			System.out.println("The version number changed");
     			if (htmlpar.exists())	DeleteRecursive(htmlpar); 
     			untarTGzFile(mnact);
	        	
	        	} catch (Exception e) {
	        		System.out.println( "File I/O error " + e);
	        	}
     	
     }
     
     
     public static void untarTGzFile(Context mnact) throws IOException {
     	
 		String destFolder = mnact.getFilesDir().getAbsolutePath();
 		FileInputStream zis = (mnact.getAssets().openFd("rootpack.targz")).createInputStream();

 		TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(zis)));
 		tis.setDefaultSkip(true);
 		untar(mnact, tis, destFolder);

 		tis.close();
 	}
     
     
     
     private static void untar(Context mnact, TarInputStream tis, String destFolder) throws IOException {
 		BufferedOutputStream dest = null;

 		TarEntry entry;
 		while ((entry = tis.getNextEntry()) != null) {
// 			System.out.println("Extracting: " + entry.getName());
 			int count;
 			byte data[] = new byte[BASE_BLOCKSIZE];

 			if (entry.isDirectory()) {
 				new File(destFolder + "/" + entry.getName()).mkdirs();
 				continue;
 			} else {
 				int di = entry.getName().lastIndexOf('/');
 				if (di != -1) {
 					new File(destFolder + "/" + entry.getName().substring(0, di)).mkdirs();
 				}
 			}

 			FileOutputStream fos = new FileOutputStream(destFolder + "/" + entry.getName());
 			dest = new BufferedOutputStream(fos);

 			while ((count = tis.read(data)) != -1) {
 				dest.write(data, 0, count);
 			}

 			dest.flush();
 			dest.close();
 		}
 	}
     
     
     static public File targetCopyFile(String copyfile) {
     	File dirfile, targfile;
 		String fileonly, dir;
 		
 		fileonly = copyfile;
 		int xind = fileonly.lastIndexOf("/");   		
 		if (xind >= 0){
 			 fileonly = fileonly.substring(xind+1);
 		}
 		
 		dir = copyfile.substring(0, xind);
 		
 //		System.out.println("targetCopy dir : "+dir+"  file : "+fileonly);
     	dirfile = new File(dir);
     	if (!dirfile.exists()) {		
     		dirfile.mkdirs();
  //   		System.out.println("targetCopy dir is created");
     	}
     	
     	targfile = new File(dirfile,fileonly);
     	
     	if (targfile.exists()){
 //    		System.out.println("file exists : "+ targfile.getPath());
     		targfile.delete();
     	}
     	
     	return (new File(dirfile,fileonly));
     }
     
     
     static public void downloadFile(Context context, long id, String uri){
     	HttpURLConnection con = null;
        File downfl = null;
        byte [] xbuf = new byte[BASE_BLOCKSIZE];
    	
    	try {
    		System.out.println("HttpAdImage : "+uri);
    		downfl = Util.targetCopyFile(context.getFilesDir()+uri);
 
    		URL url = new URL(HTTP_PROT, SOURCE_ADDRESS, Uri.encode(uri));
    		con = (HttpURLConnection) url.openConnection();
    		
    		InputStream httpin = (InputStream) con.getInputStream();
    	    FileOutputStream downflout = new FileOutputStream(downfl); 
    	    
    	    // Transfer bytes from in to out
    	    System.out.println("Start transfer");
    	    Integer fbytes = 0;
    	    int len;
    	    while ((len = httpin.read(xbuf)) > 0) {
    	        downflout.write(xbuf, 0, len);
    	        fbytes += len;
    	    }
    	    httpin.close();
    	    downflout.close();
    	    
    	    SQLHelper.setAdvertStatus(id, "A");		//Make advert active
    	    System.out.println("Done transfer");
    	}
    	catch (Exception ex) { 
    		System.out.println("Exception caught 1 : " + ex); 
    	}

    	finally {
    		if (con != null) {
    			con.disconnect();	
    		} else {
    			System.out.println("con null 2"); 
    		}
    	}
     }
     
     
     static public long getTimeNow(){
    	 
 		Time tm = new Time();
 		tm.setToNow();
 		return(tm.toMillis(true));
     }
     
     
     static public String JSONReturn(Boolean val){
    	 
    	 return("{\"rtn\":" + val + "}");
     }
     
     
     static	public String getWifiApIpAddress() {
    		
 	    try {
 	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
 	        		en.hasMoreElements();) {
 	        	
 	            NetworkInterface intf = en.nextElement();
 	            
 	            if (intf.getName().contains("wlan") || intf.getName().contains("eth0")) {
 	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
 	                        .hasMoreElements();) {
 	                    InetAddress inetAddress = enumIpAddr.nextElement();
 	                    if (!inetAddress.isLoopbackAddress()
 	                            && (inetAddress.getAddress().length == 4)) {
// 	                    	System.out.println("AP address : " + inetAddress.getHostAddress());
 	                        return inetAddress.getHostAddress();
 	                    }
 	                }
 	            }
 	        }
 	    } catch (SocketException ex) {
 	    	System.out.println("AP exception : " + ex);
 	    }
 	    return null;
 	}
     
     
     static public String getHTTPAddress(Context context){
    	 
    	 String ipad = "localhost";
    	 
    	 if (Prefs.getIPaddress(context)){
			ipad = Util.getWifiApIpAddress();
			 
    	 } 
    	 return(ipad);
     }
     
     
     public static boolean isWifiConected(Context context) {
     	
    	 WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	 
    	 if (wifiMgr != null && wifiMgr.isWifiEnabled()){
    		 
    		 ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
 
    		 if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
 //   			 System.out.println("WiFi is connected.");
	             return(true);
    		 }
    	 }
 //   	 System.out.println("WiFi NOT connected.");
     	return(false);
     }
}
