package cn.joy.framework.kits;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Map;

public class FileKit {

	public static boolean isDirExisted(String dir) {
		File dirname = new File(dir);
		return dirname.isDirectory();
	}

	public static synchronized boolean mkdirs(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			mkdirs(file);
		return true;
	}

	public static synchronized boolean mkdirs(File filePath) {
		if (!filePath.exists()) {
			String parent = filePath.getParent();
			return new File(parent).mkdirs();
		} else {
			return false;
		}
	}

	public static synchronized void mkRealDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			String parent = file.getParent();
			File p = new File(parent);
			if (!p.exists()) {
				mkRealDir(parent);
				mkRealDir(dirPath);
			} else {
				file.mkdirs();
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

	public static void copy(String src, String target) throws IOException {
		copy(src, target, null);
	}

	public static void copy(String src, String target, String excludeExt) throws IOException {
		copy(src, target, excludeExt, true);
	}

	public static void copy(String src, String target, String excludeExt, boolean coverExist) throws IOException {
		File srcFile = new File(src);
		if (srcFile.exists()) {
			if (srcFile.isFile()) {
				if (srcFile.getName().equalsIgnoreCase("Thumbs.db"))
					return;
				if (excludeExt != null && srcFile.getName().endsWith("." + excludeExt))
					return;
				BufferedOutputStream bos = null;
				BufferedInputStream bis = null;
				try {
					int bytesRead;
					byte[] buf = new byte[8096];

					mkdirs(src);
					mkdirs(target);

					File targetFile = new File(target);
					if (targetFile.exists() && targetFile.isDirectory()) {
						targetFile = new File(targetFile, srcFile.getName());
					}
					if (!coverExist && targetFile.exists() && targetFile.isFile()) {
						System.out.println("cover=" + coverExist + ", file=" + targetFile.getAbsolutePath()
								+ ", exist=" + targetFile.exists());
						return;
					}

					bos = new BufferedOutputStream(new FileOutputStream(targetFile));
					bis = new BufferedInputStream(new FileInputStream(new File(src)));
					while ((bytesRead = bis.read(buf)) >= 0) {
						bos.write(buf, 0, bytesRead);
					}
					bos.flush();
				} finally {
					if (bos != null) {
						bos.close();
					}
					if (bis != null) {
						bis.close();
					}
				}
			} else {
				// crate tareget dir;
				new File(target).mkdir();

				// copy sub files and directories;
				char ch = src.charAt(src.length() - 1);
				if (ch != '\\' && ch != '/') {
					src += File.separatorChar;
				}

				ch = target.charAt(target.length() - 1);
				if (ch != '\\' && ch != '/') {
					target += File.separatorChar;
				}

				String[] subs = srcFile.list();
				for (int i = 0; i < subs.length; i++) {
					copy(src + subs[i], target + subs[i], excludeExt, coverExist);
				}
			}
		}
	}

	public static String replace(String src, String prefix, String suffix, Map props) {
		int index1;
		int index2;
		int len1 = prefix.length();
		int len2 = suffix.length();

		StringBuffer sb = new StringBuffer();

		index1 = src.indexOf(prefix);
		while (index1 >= 0) {
			sb.append(src.substring(0, index1));
			src = src.substring(index1 + len1);
			if (src.startsWith(prefix)) {
				sb.append(prefix);
				break;
			} else {
				index2 = src.indexOf(suffix);
				if (index2 >= 0) {
					String t = src.substring(0, index2);
					Object o = props.get(t);
					String sp = (o == null ? "" : o.toString());
					sb.append(sp);
					src = src.substring((index2 + len2));
					index1 = src.indexOf(prefix);
				} else {
					sb.append(prefix);
					break;
				}
			}
		}
		sb.append(src);
		return new String(sb);
	}

	public static void replaceFile(String filePath, String prefix, String suffix, Map props) throws IOException {
		CharArrayWriter caw = new CharArrayWriter(8096);
		BufferedReader r = new BufferedReader(new FileReader(filePath));

		// read and replace symbols in the file;
		String line = r.readLine();
		while (line != null) {
			String rl = replace(line, prefix, suffix, props);
			caw.write(rl);
			caw.write("\n");
			line = r.readLine();
		}
		r.close();

		caw.flush();
		char[] ca = caw.toCharArray();
		caw.close();

		// write back the replaced contents to the orinal files;
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		bw.write(ca);
		bw.flush();
		bw.close();
	}

	public static Properties readPropFile(String filePath, String prefix, String suffix, Map props) throws IOException {
		return readPropFile(new File(filePath), prefix, suffix, props);
	}

	public static Properties readPropFile(File file, String prefix, String suffix, Map props) throws IOException {

		Properties p = new Properties();
		p.load(new FileInputStream(file));

		// read and replace symbols in the file;
		Enumeration enum2 = p.keys();
		while (enum2.hasMoreElements()) {
			String key = (String) enum2.nextElement();
			String value = (String) p.get(key);
			value = replace(value, prefix, suffix, props);
			p.setProperty(key, value);
		}

		// store the replaced properties;
		return p;
	}

	public static List<File> filerFiles(String path, String suffix) {
		return filterFiles(path, suffix, false);
	}

	public static List<File> filterFiles(String path, String suffix, boolean recusive) {
		int index = 0;
		File f = new File(path);
		if (!f.exists()) {
			return null;
		} else {
			List<File> files = new ArrayList<File>();
			File[] children = f.listFiles();
			int count = children.length;
			File[] is = new File[count];
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

	public static String getFileName(File file) {
		if (file == null) {
			return null;
		} else {
			return getFileName(file.getAbsolutePath());
		}
	}

	public static String getExtName(String fileName) {
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

	public static String readFirstLineFromFile(String filePath) throws IOException {
		String info = "-1";
		mkdirs(filePath);
		System.out.println("=filePath=" + filePath);
		File f = new File(filePath);
		// writeInfoToFileNotAppend("",filePath);
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		info = raf.readLine();
		// info=raf.readUTF();
		raf.close();
		System.out.println("==info==" + info);
		if (info == null) {
			info = "1";
			System.out.println("==set info==" + info);
			writeInfoToFileNotAppend("1", filePath);
		}
		return info;

	}

	public static boolean writeInfoToFileNotAppend(String info, String filePath) {
		try {
			mkdirs(filePath);
			File f = new File(filePath);
			FileOutputStream fos = new FileOutputStream(f, false);
			byte[] b = info.getBytes();
			fos.write(b);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String readInfoFromFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		try {
			mkdirs(filePath);
			File f = new File(filePath);
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			String s = "";
			while ((s = raf.readLine()) != null) {
				sb.append(s + "\r\n");
			}
			raf.close();
		} catch (Exception ex) {

		}
		return sb.toString();
	}

	public static boolean writeInfoToFile(String info, String filePath) {
		try {
			mkdirs(filePath);
			File f = new File(filePath);
			FileOutputStream fos = new FileOutputStream(f);
			byte[] b = info.getBytes();
			fos.write(b);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static void modifyFileCharset(String filepath, String srcset, String targetset) {
		File file = new File(filepath);
		if (!file.exists())
			return;
		if ("\\".equals(File.separator))
			return;
		File tmpFile = new File(filepath + ".tmp");
		file.renameTo(tmpFile);
		try {
			InputStream in = new FileInputStream(tmpFile);
			OutputStream out = new FileOutputStream(file);

			Reader r = new BufferedReader(new InputStreamReader(in, srcset));
			Writer w = new BufferedWriter(new OutputStreamWriter(out, targetset));

			char[] buffer = new char[4096];
			int len;
			while ((len = r.read(buffer)) != -1)
				w.write(buffer, 0, len);
			r.close();
			w.flush();
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tmpFile.delete();
	}

	public static boolean rename(File srcFile, File distFile) {
		if (!srcFile.exists())
			return false;
		else {
			return srcFile.renameTo(distFile);
		}
	}

	public static boolean rename(String src, String dist) {
		return rename(new File(src), new File(dist));
	}

}