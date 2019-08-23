package com.miracle.wtx.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ibm.websphere.dtx.dtxpi.MAdapter;
import com.ibm.websphere.dtx.dtxpi.MCard;
import com.ibm.websphere.dtx.dtxpi.MConstants;
import com.ibm.websphere.dtx.dtxpi.MMap;
import com.ibm.websphere.dtx.dtxpi.MStream;

@Component
public class WtxTransformer {
	@Autowired
	JmsTemplate jmsTemplate;

	public void transformMap(String payload) {
		try {
			// Initialize the API
			MMap.initializeAPI(null);

			// Load a local map file
			// FileInputStream fis = new
			// FileInputStream("C:\\Users\\pnadipalli\\Desktop\\WTXExample\\WTX_Functions.mmc");
			FileInputStream fis = new FileInputStream(
					"C:\\Users\\pnadipalli\\Desktop\\WTXExample\\Maps\\SPLIT_GROUPING\\SPLIT_GROUPING.mmc");
			byte[] mapData = new byte[fis.available()];
			fis.read(mapData);
			fis.close();

			// Create a map
			MMap map = new MMap("WTX_Functions", null, mapData);

			// Override the input card so that a local file
			// containing input data can be sent to the remote server
			MCard card = map.getInputCardObject(1);
			card.overrideAdapter(null, MConstants.MPI_ADAPTYPE_STREAM);

			byte[] inputData = payload.getBytes();

			// Pass it to the server via a stream
			MAdapter adapter = card.getAdapter();
			MStream stream = adapter.getOutputStream();
			stream.write(inputData, 0, inputData.length);

			// Get the adapter object handle for output card #1
			card = map.getOutputCardObject(1);

			// Override the adapter in output card #1 to be a stream
			card.overrideAdapter(null, MConstants.MPI_ADAPTYPE_STREAM);

			// Run the map
			map.run();

			// Check the return status
			int iRC = map.getIntegerProperty(MConstants.MPIP_OBJECT_ERROR_CODE, 0);
			String szMsg = map.getTextProperty(MConstants.MPIP_OBJECT_ERROR_MSG, 0);
			System.out.println("Map status: " + szMsg + " (" + iRC + ")");

			// Get the adapter object handle for output card #1
			adapter = card.getAdapter();
			stream = adapter.getInputStream();

			// Get the data in pieces from the stream
			stream.seek(0, MConstants.MPI_SEEK_SET);
			while (true) {
				boolean bIsEnd = stream.isEnd();

				// Clean and Break
				if (bIsEnd) {
					stream.setSize(0);
					break;
				}

				byte[] page = stream.readPage();
				System.out.println(new String(page));

				jmsTemplate.convertAndSend("WTX.MAP.OUT", new String(page).toString());

			}

			// Clean up
			map.unload();
			MMap.terminateAPI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
