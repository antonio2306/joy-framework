package cn.joy.framework.kits;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class FileKit {

	public void copyFileByNIO(File source, File target) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(source);
			fo = new FileOutputStream(target);
			in = fi.getChannel();
			out = fo.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void copyFileByNIOBuffer(File source, File target) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(source);
			fo = new FileOutputStream(target);
			in = fi.getChannel();
			out = fo.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(4096);
			while (in.read(buffer) != -1) {
				buffer.flip();
				out.write(buffer);
				buffer.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void copyFile(File source, File target) {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(source));
			fos = new BufferedOutputStream(new FileOutputStream(target));
			byte[] buf = new byte[4096];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean delete(String path) {
		return delete(new File(path));
	}

	public static boolean delete(File f) {
		boolean deleted = false;
		if (f.exists()) {
			if (f.isFile()) {
				deleted = f.delete();
			} else {
				File[] subs = f.listFiles();
				if (subs != null && subs.length > 0) {
					for (int i = 0; i < subs.length; i++) {
						delete(subs[i]);
					}
				}
				deleted = f.delete();
			}
		}
		return deleted;
	}

	public static List<File> filerFiles(String path, String suffix) {
		return filterFiles(path, suffix, false);
	}

	public static List<File> filterFiles(String path, String suffix, boolean recusive) {
		File f = new File(path);
		if (!f.exists()) {
			return null;
		} else {
			List<File> files = new ArrayList<File>();
			File[] children = f.listFiles();
			int count = children.length;
			for (int i = 0; i < count; i++) {
				File mapping = children[i];
				if (mapping.isFile()) {
					String name = mapping.getName();
					if (name.toLowerCase().endsWith(suffix)) {
						files.add(mapping);
					}
				} else {
					if (recusive) {
						List<File> subfiles = filterFiles(children[i].getAbsolutePath(), suffix, recusive);
						if (subfiles != null)
							files.addAll(subfiles);
					}
				}
			}
			return files;
		}
	}

	public static String getFileName(String filePath) {
		if (filePath == null) {
			return null;
		} else {
			int index = filePath.lastIndexOf('\\');
			if (index >= 0) {
				return filePath.substring(index + 1);
			} else {
				index = filePath.lastIndexOf('/');
				if (index >= 0) {
					return filePath.substring(index + 1);
				} else {
					return filePath;
				}
			}
		}
	}

	public static String getFileExt(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index >= 0) {
			return fileName.substring(index + 1);
		} else {
			return null;
		}
	}

	public static String getRegularPath(String path) {
		if (path == null || path.length() < 1) {
			return path;
		} else {
			char ch = path.charAt(path.length() - 1);
			if (ch != '\\' && ch != '/') {
				return path + "/";
			} else {
				return path;
			}
		}
	}

	public static byte[] readFileBytes(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	public static byte[] readFileBytesByNIO(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}

		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] readFileBytesByMapped(String filePath) throws IOException {
		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filePath, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String readFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		String strContent;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((strContent = br.readLine()) != null) {
				sb.append(strContent).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String readFile(String filePath, String charset) throws IOException {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		if (charset == null || charset.length() == 0)
			charset = "UTF-8";
		String strContent;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			while ((strContent = br.readLine()) != null) {
				sb.append(strContent).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static File createFile(String filePath){
		File file = new File(filePath);
		if(file.exists())
			return file;
		File dir = file.getParentFile();
		if(dir.exists() || dir.mkdirs()){
			try {
				if(file.createNewFile())
					return file;
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public static void writeInfoToFile(String info, String filePath) {
		FileOutputStream fos = null;
		try {
			File file = createFile(filePath);
			fos = new FileOutputStream(file);
			fos.write(info.getBytes());
			fos.flush();
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeInfoToFile(String info, String filePath, String charset) {
		BufferedWriter output = null;
		try {
			if (charset == null || charset.length() == 0)
				charset = "UTF-8";
			File file = createFile(filePath);
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
			output.write(info);
		} catch (Exception e) {
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void changeFileCharset(String filePath, String srcCharset, String targetCharset) {
		File file = new File(filePath);
		if (!file.exists())
			return;
		if ("\\".equals(File.separator))
			return;
		File tmpFile = new File(filePath + ".tmp");
		file.renameTo(tmpFile);

		InputStream in = null;
		OutputStream out = null;
		Reader r = null;
		Writer w = null;
		try {
			in = new FileInputStream(tmpFile);
			out = new FileOutputStream(file);
			r = new BufferedReader(new InputStreamReader(in, srcCharset));
			w = new BufferedWriter(new OutputStreamWriter(out, targetCharset));

			char[] buffer = new char[4096];
			int len;
			while ((len = r.read(buffer)) != -1)
				w.write(buffer, 0, len);
			w.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if(r!=null) r.close(); } catch (IOException e) { e.printStackTrace(); }
			try { if(in!=null) in.close(); } catch (IOException e) { e.printStackTrace(); }
			try { if(w!=null) w.close(); } catch (IOException e) { e.printStackTrace(); }
			try { if(out!=null) out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		tmpFile.delete();
	}

	public static void downloadFile(HttpServletResponse response, Map<String, Object> fileInfo) {
		String contentType = StringKit.getString(fileInfo.get("contentType"), "application/x-msdownload");
		response.setContentType(contentType); // response.setContentType("application/force-download");

		Object fileObj = fileInfo.get("file");
		if (fileObj instanceof File) {
			BufferedInputStream bis = null;
			OutputStream out = null;

			try {
				File file = (File) fileObj;
				if (!file.exists())
					return;
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition",
						"attachment;filename=" + StringKit.getString(fileInfo.get("displayName"), file.getName()));

				bis = new BufferedInputStream(new FileInputStream(file));
				out = response.getOutputStream();

				int bytesRead = 0;
				byte[] buffer = new byte[4096];
				while ((bytesRead = bis.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { if(out!=null) out.close(); } catch (IOException e) { e.printStackTrace(); }
				try { if(bis!=null) bis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}

	}
}