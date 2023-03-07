package com.application.archive;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for I/O operations.
 */
public final class IOUtils {

    /**
     * Default buffer size used for {@code copy} operations.
     */
    private static final int DEFAULT_BUFFER_SIZE = 8024;

    private IOUtils() {

    }

    /**
     * Copies the content of an InputStream into a destination File.
     * 
     * @param source the InputStream to copy
     * @param destination the target File
     * @throws IOException if an error occurs
     */
    public static void copy(InputStream source, File destination) throws IOException {
        OutputStream output = null;

        try {
            destination.getParentFile().mkdirs();
            output = new FileOutputStream(destination);
            copy(source, output);
        } finally {
            closeQuietly(output);
        }
    }

    /**
     * Copies the content of a InputStream into an OutputStream. Uses a default buffer size of 8024 bytes.
     * 
     * @param input the InputStream to copy
     * @param output the target Stream
     * @return the amount of bytes written
     * @throws IOException if an error occurs
     */
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies the entire content of the given InputStream into the given OutputStream.
     * 
     * @param input the InputStream to copy
     * @param output the target Stream
     * @param buffersize the buffer size to use
     * @return the amount of bytes written
     * @throws IOException if an error occurs
     */
    public static long copy(final InputStream input, final OutputStream output, int buffersize) throws IOException {
        final byte[] buffer = new byte[buffersize];
        int n;
        long count = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Computes the path name of a file node relative to a given root node.
     * <br>
     * If the root is {@code /home/cdlflex/custom-ahy} and the given node is
     * {@code /home/cdlflex/custom-ahy/assembly/pom.xml}, the returned path name will be {@code assembly/pom.xml}.
     * 
     * @param root the parent node
     * @param node the file node to compute the relative path for
     * @return the path of {@code node} relative to {@code root}
     * @throws IOException when an I/O error occurs during resolving the canonical path of the files
     */
    public static String relativePath(File root, File node) throws IOException {
        String rootPath = root.getCanonicalPath();
        String nodePath = node.getCanonicalPath();

        return nodePath.substring(rootPath.length() + 1);
    }

    /**
     * Makes sure that the given {@link File} is either a directory, or that it does not exist and a directory
     * can be created at its path.
     * <br>
     * Will throw an exception if the given {@link File} is actually an existing file
     * 
     * @param destination the directory which to ensure its existence for
     * @throws IOException if an I/O error occurs e.g. when attempting to create the destination directory
     * @throws IllegalArgumentException if the destination is an existing file
     */
    public static void requireDirectory(File destination) throws IOException, IllegalArgumentException {
        if (destination.isFile()) {
            throw new IllegalArgumentException(destination + " exists and is a file, directory or path expected.");
        } else if (!destination.exists()) {
            destination.mkdirs();
        } else if (!destination.canWrite()){
            throw new IllegalArgumentException(destination + " is not writeable.");
        }
    }

    /**
     * Null-safe method that calls {@link java.io.Closeable#close()} and chokes the IOException.
     * 
     * @param closeable the object to close
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Given a source File, return its direct descendants if the File is a directory. Otherwise return the File itself.
     *
     * @param source File or folder to be examined
     * @return a File[] array containing the files inside this folder, or a size-1 array containing the file itself.
     */
    public static File[] filesContainedIn(File source) {
        if (source.isDirectory()) {
            return source.listFiles();
        } else {
            return new File[] { source };
        }
    }

}
