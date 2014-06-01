package com.adserv.adladl;


import java.io.File;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.GZIPInputStream;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import static com.adserv.adladl.Const.HTML_DIR;
import static com.adserv.adladl.Const.USERHTML_DIR;
import static com.adserv.adladl.Const.BASE_BLOCKSIZE;


import com.adserv.adladl.MainActivity;


public class Util {

	
	 static   void DeleteRecursive(File fileOrDirectory) {
		 
		 System.out.println("IN deleteRecursive : " + fileOrDirectory.getAbsolutePath());
	            if (fileOrDirectory.isDirectory())
	                for (File child : fileOrDirectory.listFiles())
	                    DeleteRecursive(child);

	            fileOrDirectory.delete();
	        }
	 
	 
     public static void versionChangeHTML(MainActivity mnact ) {
     	
//    		if (FS_Util.changedVersionNumber(mnact)) {
 		if (true) {
     	
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
     	
    		} else {
 			System.out.println("The ver num is the same");
 		}
     }
     
     
     public static void untarTGzFile(MainActivity mnact) throws IOException {
     	
 		String destFolder = mnact.getFilesDir().getAbsolutePath();
 		FileInputStream zis = (mnact.getAssets().openFd("rootpack.targz")).createInputStream();

 		TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(zis)));
 		tis.setDefaultSkip(true);
 		untar(mnact, tis, destFolder);

 		tis.close();
 	}
     
     
     
     private static void untar(MainActivity mnact, TarInputStream tis, String destFolder) throws IOException {
 		BufferedOutputStream dest = null;

 		TarEntry entry;
 		while ((entry = tis.getNextEntry()) != null) {
 			System.out.println("Extracting: " + entry.getName());
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
     
}
