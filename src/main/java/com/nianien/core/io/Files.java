package com.nianien.core.io;

import com.nianien.core.exception.ExceptionHandler;
import com.nianien.core.function.Function;
import com.nianien.core.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 处理文件操作的工具类
 *
 * @author skyfalling
 */
public class Files {


    /**
     * 换行符, windows:"\r\n", linux: "\n"
     */
    public final static String newLine = System.getProperty("line.separator");
    /**
     * 缓冲区大小
     */
    private final static int bufferSize = 1024 * 8;


    /**
     * 将源文件src移动至目标文件dest所表示的路径<br/>
     * 这里dest对应文件为移动后的路径,如果目标路径存在,则会被覆盖
     *
     * @param src
     * @param dest
     * @return 如果移动成功返回true, 否则返回false
     */
    public static boolean move(File src, File dest) {
        File dir = dest.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dest.exists()) {
            delete(dest);
        }
        return src.renameTo(dest);
    }

    /**
     * 复制源文件src文件至目标我呢就dest所表示的路径,支持目录拷贝<br/>
     * 这里dest对应文件为复制后的路径,如果目标路径存在,则会被覆盖
     *
     * @param src
     * @param dest
     */
    public static void copy(File src, File dest) {
        File dir = dest.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dest.exists()) {
            delete(dest);
        }
        //这里定义copy0方法,是为了不用每次都判断目标路径是否存在
        copy0(src, dest);
    }

    /**
     * 递归复制文件
     *
     * @param src
     * @param dest
     */
    private static void copy0(File src, File dest) {
        if (src.isDirectory()) {
            dest.mkdirs();
            File[] files = src.listFiles();
            for (File file : files) {
                copy(file, new File(dest, file.getName()));
            }
        } else {
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                inChannel = new FileInputStream(src).getChannel();
                outChannel = new FileOutputStream(dest).getChannel();
                // 8M缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                while (inChannel.read(buffer) != -1) {
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.clear();//prepare for reading;清空缓冲区；
                }
            } catch (Exception e) {
                ExceptionHandler.throwException(e);
            } finally {
                Closer.close(inChannel, outChannel);
            }
        }
    }

    /**
     * 删除指定文件,支持目录操作<br/>
     * 如果指定路径为文件夹,则递归删除子文件夹及其内容
     *
     * @param file
     * @return 如果删除成功, 返回true, 否则返回false. 如果文件不存在,返回true
     */
    public static boolean delete(File file) {
        if (!file.exists())
            return true;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                if (!delete(f))
                    return false;
        }
        return file.delete();
    }

    /**
     * 获取文件内容的字节数组
     *
     * @param file
     * @return 表示文件内容的字节数组
     */
    public static byte[] getBytes(File file) {
        try {
            return getBytes(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }


    /**
     * 获取InputStream对象输出的字节数组
     *
     * @param inputStream
     * @return
     */
    public static byte[] getBytes(InputStream inputStream) {
        try {
           /* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            byte[] buffer = new byte[bufferSize];
            while ((read = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            return byteArrayOutputStream.toByteArray();*/
            List<byte[]> buffers = new ArrayList<byte[]>();
            int size = 0;
            int read;
            byte[] buffer = new byte[bufferSize];
            while ((read = inputStream.read(buffer)) != -1) {
                size += read;
                buffers.add(Arrays.copyOf(buffer, read));
            }
            byte[] result = new byte[size];
            int position = 0;
            for (byte[] byteArray : buffers) {
                System.arraycopy(byteArray, 0, result, position, byteArray.length);
                position += byteArray.length;
            }
            return result;
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        } finally {
            Closer.close(inputStream);
        }
    }


    /**
     * 读取文件文本内容,默认编码
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        try {
            return read(new FileReader(file));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定编码格式读取文件文本内容
     *
     * @param file
     * @param charset 文本编码格式
     * @return
     */
    public static String read(File file, String charset) {
        try {
            return read(new InputStreamReader(new FileInputStream(file),
                    charset));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 读取InputStream对象的输出文本,读取后关闭InputStream对象
     *
     * @param inputStream
     * @return
     */
    public static String read(InputStream inputStream) {
        return read(new InputStreamReader(inputStream));
    }

    /**
     * 以指定编码格式读取InputStream对象的输出文本内容,读取后关闭InputStream对象
     *
     * @param inputStream
     * @param charset     文本编码格式
     * @return
     */
    public static String read(InputStream inputStream, String charset) {
        try {
            return read(new InputStreamReader(inputStream, charset));
        } catch (UnsupportedEncodingException e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 读取Reader对象的文本内容,读取后关闭Reader对象
     *
     * @param reader
     * @return
     */
    public static String read(Reader reader) {
        final StringBuilder sb = new StringBuilder();
        readLines(reader, new Function<String, String>() {
            @Override
            public String apply(String s) {
                sb.append(s).append(newLine);
                return null;
            }
        });
        return sb.toString();
    }

    /**
     * 按行读取文件内容
     *
     * @param file
     * @return 按行返回文本内容列表
     */
    public static List<String> readLines(File file) {
        try {
            return readLines(new FileReader(file));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定字符编码格式按行读取文件内容
     *
     * @param file
     * @param charset
     * @return 按行返回文本内容列表
     */
    public static List<String> readLines(File file, String charset) {
        try {
            return readLines(
                    new InputStreamReader(new FileInputStream(file), charset));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 读取文件对象,处理每行的文本内容
     *
     * @param file
     * @param function
     */
    public static void readLines(File file, Function<String, String> function) {
        try {
            readLines(new FileReader(file), function);
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定字符编码格式读取文件对象,处理每行的文本内容
     *
     * @param file
     * @param function
     * @param charset
     */
    public static void readLines(File file, Function<String, String> function,
                                 String charset) {
        try {
            readLines(
                    new InputStreamReader(new FileInputStream(file), charset),
                    function);
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 按行读取InputStream对象的文本内容, 然后关闭InputStream对象
     *
     * @param inputStream
     * @return 按行返回文本内容列表
     */
    public static List<String> readLines(InputStream inputStream) {
        return readLines(new InputStreamReader(inputStream));
    }

    /**
     * 读取InputStream对象内容的每行内容, 然后关闭InputStream对象
     *
     * @param inputStream
     * @param function
     */
    public static void readLines(InputStream inputStream, Function<String, String> function) {
        readLines(new InputStreamReader(inputStream), function);
    }


    /**
     * 以指定编码格式按行读取InputStream对象的文本内容, 然后关闭InputStream对象
     *
     * @param inputStream
     * @param charset
     * @return 按行返回文本内容列表
     */
    public static List<String> readLines(InputStream inputStream,
                                         String charset) {
        try {
            return readLines(new InputStreamReader(inputStream, charset));
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定编码格式读取InputStream对象,按行处理文本内容 然后关闭InputStream对象
     *
     * @param inputStream
     * @param function
     * @param charset
     */
    public static void readLines(InputStream inputStream, Function<String, String> function,
                                 String charset) {
        try {
            readLines(new InputStreamReader(inputStream, charset), function);
        } catch (Exception e) {
            throw ExceptionHandler.throwException(e);
        }
    }


    /**
     * 读取Reader对象内容的每行内容, 然关闭reader对象
     *
     * @param reader
     * @return 按行返回文本内容列表
     */
    public static List<String> readLines(Reader reader) {
        final List<String> lines = new ArrayList<String>();
        readLines(reader, new Function<String, String>() {
            @Override
            public String apply(String s) {
                lines.add(s);
                return null;
            }
        });
        return lines;
    }

    /**
     * 读取Reader对象,按行处理文本内容, 然关闭reader对象
     *
     * @param reader
     * @param function
     */
    public static void readLines(Reader reader, Function<String, String> function) {
        try {
            BufferedReader buffer = new BufferedReader(reader);
            String line;
            while ((line = buffer.readLine()) != null) {
                function.apply(line);
            }
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        } finally {
            Closer.close(reader);
        }
    }


    /**
     * 向文件对象写入指定内容
     *
     * @param file     目标文件
     * @param content  待写入文本
     * @param isAppend 是否追加
     */
    public static void write(File file, String content, boolean isAppend) {
        try {
            createParent(file);
            write(new FileWriter(file, isAppend), content);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定编码格式向文件对象写入文本内容
     *
     * @param file     目标文件
     * @param content  待写入文本
     * @param charset  编码格式
     * @param isAppend 是否追加
     */
    public static void write(File file, String content, String charset,
                             boolean isAppend) {
        try {
            createParent(file);
            OutputStream outputStream = new FileOutputStream(file, isAppend);
            Writer writer = new OutputStreamWriter(outputStream, charset);
            write(writer, content);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 读取InputStream对象,写入OutputStream对象, 然后关闭OutputStream和InputStream对象
     *
     * @param outputStream
     * @param inputStream
     */
    public static void write(OutputStream outputStream, InputStream inputStream) {
        try {
            int read;
            byte[] buffer = new byte[bufferSize];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
                outputStream.flush();
            }
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        } finally {
            Closer.close(outputStream);
            Closer.close(inputStream);
        }
    }

    /**
     * 向OutputStream对象写入文本内容,然后关闭OutputStream对象
     *
     * @param outputStream
     * @param content
     */
    public static void write(OutputStream outputStream, String content) {
        try {
            write(new OutputStreamWriter(outputStream), content);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 以指定编码格式向OutputStream对象写入文本内容,然后关闭OutputStream对象
     *
     * @param outputStream
     * @param content
     * @param charset
     */
    public static void write(OutputStream outputStream, String content,
                             String charset) {
        try {
            write(new OutputStreamWriter(outputStream, charset), content);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 读取reader对象, 写入writer对象,然后关闭Writer和Reader对象
     *
     * @param writer
     * @param reader
     */
    public static void write(Writer writer, Reader reader) {
        try {
            char[] buffer = new char[bufferSize];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
                writer.flush();
            }
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 向Writer对象写入文本内容,然后关闭Writer对象
     *
     * @param writer
     * @param content
     */
    public static void write(Writer writer, String content) {
        writer = new BufferedWriter(writer);
        try {
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        } finally {
            Closer.close(writer);
        }
    }


    /**
     * 获取URL对象对应的系统路径,如果是压缩包,则获取压缩包所在路径
     *
     * @param url
     * @return URL对象对应的系统路径
     */
    public static String urlToPath(URL url) {
        String path = url.getPath();
        int location = path.indexOf("!/");
        if (location != -1) {
            path = path.substring(0, location);
        }
        return path;
    }

    /**
     * 创建指定路径所表示的目录<br/>
     * 如果路径指向已存在的非目录文件,则删除该文件并创建目录
     *
     * @param path
     */
    public static void createDirectory(File path) {
        // 如果存在且不是目录,则删除后在创建
        if (!path.isDirectory()) {
            if (path.exists()) {
                path.delete();
            }
            path.mkdirs();
        }
    }

    /**
     * 创建并返回指定文件所在的目录文件<br/>
     *
     * @param file
     * @return 文件对象
     */
    public static File createParent(File file) {
        File parent = file.getParentFile();
        createDirectory(parent);
        return parent;
    }


    /**
     * 获取指定的绝对路径,并尝试解析为标准路径
     *
     * @param path
     * @return 返回绝对路径
     */
    public static String getAbsolutePath(String path) {
        File file = new File(path);
        try {
            return file.getCanonicalPath();
        } catch (Exception e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 获取指定路径的上级路径
     *
     * @param path
     * @return 返回上一级路径
     */
    public static String getParentPath(String path) {
        return new File(path).getParent();
    }

    /**
     * 获取当前路径
     *
     * @return 当前路径
     */
    public static String getCurrentPath() {
        String current = System.getProperty("user.dir");
        if (StringUtils.isEmpty(current))
            current = getAbsolutePath(".");
        return current;
    }


    /**
     * 获取路径所在的目录<br/>
     * 如果路径本身代表目录,则返回路径本身,否则路径所在目录
     *
     * @param path
     * @return 指定路径所在的目录
     */
    public static String getDirectory(String path) {
        File file = new File(path);
        if (file.isDirectory())
            return getAbsolutePath(path);
        //路径本身表示路径
        if (path.endsWith("/") || path.endsWith("\\"))
            return file.getAbsolutePath();
        return file.getParent();
    }

    /**
     * 多个路径组合构建文件对象<br/>
     *
     * @param paths
     * @return 组合路径所表示的文件对象. 如果给定路径集合为空,则返回null
     */
    public static File getFile(String... paths) {
        File file = null;
        for (String path : paths) {
            if (file == null) {
                file = new File(path);
            } else {
                file = new File(file, path);
            }
        }
        return file;
    }


    /**
     * 获取指定路径的文件名
     *
     * @param path
     * @return 文件名称
     */
    public static String getFileName(String path) {
        return new File(path).getName();
    }

    /**
     * 获取不含后缀的文件名
     *
     * @param path
     * @return 不含后缀的文件名
     */
    public static String getFileNameWithoutExt(String path) {
        String fileName = getFileName(path);
        int index = fileName.lastIndexOf('.');
        return index == -1 ? fileName : fileName.substring(0, index);
    }

    /**
     * 获取文件名后缀
     *
     * @param path
     * @return 文件名后缀, 含"."
     */
    public static String getFileExt(String path) {
        String fileName = getFileName(path);
        int index = fileName.lastIndexOf('.');
        return index == -1 ? "" : fileName.substring(index);
    }


    /**
     * 判断指定路径是否为绝对路径
     *
     * @param path
     * @return 如果是返回true, 否则返回false
     */
    public static boolean isAbsolutePath(String path) {
        return new File(path).isAbsolute();
    }

}
