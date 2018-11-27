package io.basquiat.util;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.wallet.domain.Wallet;

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

	static String WALLET_PATH;

	static String COINBASE_PATH;

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
	
	@Value("${wallet.path}")
	private void setWalletPath(String walletPath) {
		WALLET_PATH = walletPath;
    }

	@Value("${coinbase.path}")
	private void setCoinbasePath(String coinbasePath) {
		COINBASE_PATH = coinbasePath;
    }
	
	/**
	 * write Block Json file
	 * @param block
	 */
	public static void writeJsonBlockFile(Block block) {
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
	 * read json block file and convert to object
	 * @param fileIndex
	 * @param clazz
	 * @return T
	 */
	public static Block readJsonBlockFile(Integer fileIndex) {
		ObjectMapper mapper = new ObjectMapper();
		Block block = null;
		try {
			File file = new File(BLOCK_FILE_PATH + BLOCK_FILE_NAME_PREFIX + fileIndex + BLOCK_FILE_FORMAT);
			if(file.exists()) {
				block = mapper.readValue(file, Block.class);
			} else {
				LOG.info("File doesn't Exists");
			}
			
        } catch (Exception e) {
            e.printStackTrace();  
        }
		return block;
	}
	
	/**
	 * Wallet 지갑 정보를 파일로 쓴다.
	 * @param wallet
	 */
	public static void writeJsonWalletFile(Wallet wallet) {
		try {
			File file = new File(WALLET_PATH + wallet.getAccount());
			if(!file.exists()) {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);  
				fileWriter.write(CommonUtil.convertJsonStringFromObject(wallet));  
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
	 * coinbase 파일
	 * 내용은 없고 파일명 자체가 coinbase account
	 * @param wallet
	 */
	public static void writeCoinbaseFile(String account) {
		try {
			File file = new File(COINBASE_PATH + account);
			file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * read json wallet file and convert to object
	 * @param fileIndex
	 * @param clazz
	 * @return T
	 */
	public static Wallet readJsonWalletFile(String account) {
		ObjectMapper mapper = new ObjectMapper();
		Wallet wallet = null;
		try {
			File file = new File(WALLET_PATH + account);
			if(file.exists()) {
				wallet = mapper.readValue(file, Wallet.class);
			} else {
				LOG.info("File doesn't Exists");
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		return wallet;  
	}
	
	/**
	 * account로 생성된 wallet이 있는지 확인한다.
	 * @param account
	 * @return boolean
	 */
	public static boolean hasWalletFile(String account) {
		boolean hasFile = false;
		try {
			File file = new File(WALLET_PATH + account);
			hasFile = file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return hasFile;
	}
	
	/**
	 * read json file and convert to object
	 * @param fileIndex
	 * @param clazz
	 * @return T
	 */
	public static <T> T readFile(File file, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		T object = null;
		try {
			object = (T) mapper.readValue(file, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return object;
	}
	
	/**
	 * file list size from file repository
	 * @return Integer
	 */
	public static Integer blockFileLength() {
		File[] files = FileIOUtil.blockFileList();
		return files.length;
	}

	/**
	 * get file array
	 * @return File[]
	 */
	public static File[] blockFileList() {
		File file = new File(BLOCK_FILE_PATH);
		File[] files = file.listFiles();
		return files;
	}
	
	public static String getCoinbaseAccount() {
		File file = new File(COINBASE_PATH);
		File[] files = file.listFiles();
		// 이 폴더는 오직 하나의 파일만 가질 수 있다.
		String fileName = null;
		if(files.length == 1) {
			File coinbaseAccount = files[0];
			fileName = coinbaseAccount.getName();
		}
		return fileName;
	}
	
	/**
	 * coinbase file을 삭제한다.
	 * @param account
	 */
	public static void deleteCoinbaseFile(String account) {
		File file = new File(COINBASE_PATH + account);
		if(file.exists()) {
			if(file.delete()) {
				LOG.info("delete files");
			} else {
				LOG.info("doesn't delete files");
			}
		}
	}
	
}
