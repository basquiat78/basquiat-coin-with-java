package com.basquiat.util;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.basquiat.blockchain.block.domain.Block;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * block file IO
 * 
 * created by basquiat
 *
 */
@Component
public class FileIOUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FileIOUtil.class);
	
	static String BLOCK_FILE_PATH;
	
	static String BLOCK_FILE_NAME_PREFIX;
	
	static String BLOCK_FILE_FORMAT;

	@Value("${block.file.path}")
	private void setBlockFilePath(String blockFilePath) {
		BLOCK_FILE_PATH = blockFilePath;
    }

	@Value("${block.file.name.prefix}")
	private void setBlockFileNamePrefix(String blockFileNamePrefix) {
		BLOCK_FILE_NAME_PREFIX = blockFileNamePrefix;
    }
	
	@Value("${block.file.name.format}")
	private void setBlockFileFormat(String blockFileFormat) {
		BLOCK_FILE_FORMAT = blockFileFormat;
    }
	
	/**
	 * write Block Json file
	 * @param block
	 * @throws JsonProcessingException
	 */
	public static void writeJsonFile(Block block) {
		try {
			String fileName = BLOCK_FILE_NAME_PREFIX + block.getIndex() + BLOCK_FILE_FORMAT;
			File file = new File(BLOCK_FILE_PATH + fileName);  
			if(!file.exists()) {
				file.createNewFile();  
				FileWriter fileWriter = new FileWriter(file);  
				fileWriter.write(CommonUtil.convertJsonStringFromObject(block));  
				fileWriter.flush();  
				fileWriter.close();
			} else {
				LOG.info("File Exists");
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		
	}
	
	/**
	 * read json file and convert to object
	 * @param fileIndex
	 * @param clazz
	 * @return
	 */
	public static <T> T readJsonFile(Integer fileIndex, Class<T> clazz) {

		ObjectMapper mapper = new ObjectMapper();
		T object = null;
		
		try {
			File file = new File(BLOCK_FILE_PATH + BLOCK_FILE_NAME_PREFIX + fileIndex + BLOCK_FILE_FORMAT);
			if(file.exists()) {
				object = (T) mapper.readValue(file, clazz);
			} else {
				LOG.info("File doesn't Exists");
			}
			
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return object;  
		
	}

	/**
	 * file list size from file repository
	 * @return Integer
	 */
	public static Integer fileLength() {
		File file = new File(BLOCK_FILE_PATH);
		File[] files = file.listFiles();
		return files.length;
	}
	
}
