package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * SwiftFile is a special data type which allows for the storage <br>
 * of files as bytes and Strings. Files can be written/read to/from files <br>
 * using the methods provided below. For extended capabilities, SwiftFiles <br>
 * can be sent and received over a socket using the send and receive methods. <br>
 * <br>
 * <b>Warning:</b> SwiftFile shouldn't be used by upper layers due to their exceptions. <br>
 * Instead, please use the SwiftFront object which handles IOExceptions and only throws FileExceptions
 * 
 * <br><br>
 * <b>Note:</b> I refer to a "Physical File" in my comments. This just means a file on the hard drive. <br>
 * A "virtual file" in this case would be a SwiftFile which exists in memory, but not on a hard drive.
 * 
 * @author Ryan Kerr
 */
public class SwiftFile extends Data implements Settings, Logging {
	private File   file  = null;
	private byte[] bytes = null;
	
	private boolean bset   = false,
					fset   = false;

	/** Identifies file and blank array **/
	protected SwiftFile() {
		super(DAT_FILE, 0);
	}
	
	/**
	 * Creates a SwiftFile with a default data array
	 * @param size Size of array
	 */
	public SwiftFile(int size) {
		super(DAT_FILE, size);
	}
	
	/**
	 * Creates a SwiftFile with data but no physical file.
	 * @param b Byte array (representing file contents)
	 */
	public SwiftFile(byte[] b) {
		super(DAT_FILE, 0);
		setBytes(b);
	}
	
	/**
	 * Creates a SwiftFile with data and a File to write to
	 * @param f File where byte data belongs
	 * @param b Byte array which represents file contents
	 */
	public SwiftFile(File f, byte[] b) {
		this(b);
		setFile(f);
	}
	
	/**
	 * Creates a SwiftFile with a File to read from
	 * @param p Path to File
	 * @param read Read from file on creation
	 * @throws IOException if read operation failed
	 * @throws FileException if read operation failed
	 */
	public SwiftFile(Path p, boolean read) throws IOException, FileException {
		this(p.toFile(), read);
	}
	
	/**
	 * Creates a SwiftFile with a File to read from
	 * @param path Path to File
	 * @param read Read from file on creation
	 * @throws IOException if read operation failed
	 * @throws FileException if read operation failed
	 */
	public SwiftFile(String path, boolean read) throws IOException, FileException {
		this(new File(path), read);
	}

	/**
	 * Creates a SwiftFile with a File to read from
	 * @param File Physical file
	 * @param read Read from file on creation
	 * @throws IOException if read operation failed
	 * @throws FileException if read operation failed
	 */
	public SwiftFile(File f, boolean read) throws IOException, FileException {
		this();
		setFile(f, read);
	}
	
	/**
	 * Creates a SwiftFile from plain text data
	 * @param string Array of plain text file data
	 */
	public SwiftFile(String[] string) {
		this();
		for(String s : string) add(s);
		toData();
	}
	
	/**
	 * Create a SwiftFile being transferred
	 * @param dis Stream to read from
	 * @throws IOException if something failed while reading from the socket
	 */
	public SwiftFile(DataInputStream dis) throws IOException {
		this();
		receive(dis);
	}
	
	/**
	 * Send a SwiftFile to a SwiftNetTool
	 * @param dis Stream to read from
	 * @throws IOException if something failed while reading from the socket
	 */
	public void receive(DataInputStream dis) throws IOException {
		if(dis != null) {
			int    size = dis.readInt();
			String path = dis.readUTF();
			file  = new File(path);
			bytes = new byte[size];
			for(int i = 0; i < size; i++) bytes[i] = dis.readByte();
			checkFileFlag();
			checkByteFlag();
		}
		else throw new IOException("Bad Socket");
	}
	
	/**
	 * Send a SwiftFile over a socket
	 * @param dos Stream to write from
	 * @throws IOException if something failed while writing to the socket
	 * @throws FileException if something failed while reading from a physical file
	 */
	public void send(DataOutputStream dos) throws IOException, FileException {
		if(dos != null) {
			if(!fset && !bset) {
				fromData();
			}
			if(fset && !bset) {
				read();
			}
			if(bset) {
				dos.writeInt(bytes.length);
				if(fset) dos.writeUTF(file.getPath());
				else dos.writeUTF(LC_PATH + "temp");
				for(int i = 0; i < bytes.length; i++) dos.writeByte(bytes[i]);
			}
		} 
		else throw new IOException("Bad Socket");
	}
	
	/**
	 * Read a physical file into a byte array
	 * @throws IOException if something goes wrong while reading from the file
	 * @throws FileException if File is too large or a physical file hasn't been defined
	 */
	public void read() throws IOException, FileException {
		if(fset && file.exists() && file.isFile()) {
			if(file.length() < Integer.MAX_VALUE) {
				bytes = Files.readAllBytes(file.toPath());
				checkByteFlag();
				fromData();
			}
			else throw new FileException(EXC_SIZE);
		}
		else if(!fset) throw new FileException(EXC_MISS);
		else throw new FileException(EXC_F404);
	}
	
	/**
	 * Write byte data to a physical file
	 * @param path Path to physical file to write to
	 * @param append Writes over the file if false; Adds to the file if true
	 * @throws IOException if something failed while writing to a physical file
	 * @throws FileException if the byte array is empty (nothing to write)
	 */
	public void write(Path path, boolean append) throws IOException, FileException {
		if(bset) {
			File parent = path.toFile().getParentFile();
			if((!parent.exists() || !parent.isDirectory()) && !parent.isFile())
				if(!parent.mkdirs()) throw new IOException("Failed to make parent directories");
			
			try(FileOutputStream fos = new FileOutputStream(path.toString(), append)) {
				fos.write(bytes);
			}
		}
		else throw new FileException(EXC_MISS);
	}
	
	/**
	 * Write data to a physical file (writes over)
	 * @throws IOException if something failed while writing to a physical file
	 * @throws FileException if either the byte array is empty (nothing to write) or<br>
	 * 						 there is no file to write to
	 */
	public void write() throws IOException, FileException {
		if(fset) write(file.toPath(), false);
		else throw new FileException(EXC_MISS);
	}
	
	/**
	 * Append data to a physical file (adds data)
	 * @throws IOException if something failed while appending to a physical file
	 * @throws FileException if either the byte array is empty (nothing to write) or<br>
	 * 						 there is no file to write to
	 */
	public void append() throws IOException, FileException {
		if(fset) write(file.toPath(), true);
		else throw new FileException(EXC_MISS);
	}
	
	/**
	 * Print data from the plain text data
	 * @param print Print data directly from plain text data array if true; Print from bytes if false
	 * @throws IOException if something failed while writing data to a physical file
	 * @throws FileException if there was no information to write
	 */
	public void writeFromData(boolean print) throws IOException, FileException {
		if(print && fset) {
			if(0 < getSize()) {
				try(PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
					for(String s : getArray()) writer.println(s);
					writer.flush();
				}
			}
			else throw new FileException(EXC_MISS);
		}
		else {
			fromData();
			if(bset) write(); 
			else throw new FileException(EXC_MISS);
		}
	}
	
	/** Convert plain text to byte array **/
	public void toData() {
		String build = "";
		for(String s : getArray()) build += s;
		bytes = build.getBytes(CHARSET);
		checkByteFlag();
	}
	
	/** Convert byte array to plain text **/
	public void fromData() {
		if(bset) {
			String   split = new String(bytes);
			String[] build = split.split("\n");
			setArray(build);
		}
	}
	
	/**
	 * Turns generic data into a SwiftFile
	 * @param dat Data to convert to SwiftFile
	 */
	public void convert(Data dat) {
		setArray(dat.getArray());
		toData();
	}
	
	/**
	 * Set file contents using a byte array
	 * @param b File contents as bytes
	 */
	public void setBytes(byte[] b) {
		bytes = b;
		checkByteFlag();
	}
	
	/**
	 * Get the byte array
	 * @return File contents as bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	/**
	 * Set the physical file
	 * @param f File
	 */
	public void setFile(File f) {
		file = f;
		checkFileFlag();
	}
	
	/**
	 * Point the SwiftFile to (and read from) a physical file
	 * @param p Path to File
	 * @param read Read from file if true; only set file if false
	 * @throws IOException if something fails while reading from file
	 * @throws FileException if no file to write to was defined
	 */
	public void setFile(Path p, boolean read) throws IOException, FileException {
		setFile(p.toFile(), read);
	}

	/**
	 * Set the SwiftFile to (and read from) a physical file
	 * @param path Path to File
	 * @param read Read from file if true; only set file if false
	 * @throws IOException if something fails while reading from file
	 * @throws FileException if no file to write to was defined
	 */
	public void setFile(String path, boolean read) throws IOException, FileException {
		setFile(new File(path), read);
	}
	
	/**
	 * Point the SwiftFile to (and read from) a physical file
	 * @param f File
	 * @param read Read from file if true; only set file if false
	 * @throws IOException if something fails while reading from file
	 * @throws FileException if no file to write to was defined
	 */
	public void setFile(File f, boolean read) throws IOException, FileException {
		file = f;
		checkFileFlag();
		if(read) read();
	}
	
	/**
	 * Get the file the SwiftFile points to
	 * @return file
	 */
	public File getFile() {
		return file;
	}
	
	/** See if information is present in the byte data array **/
	protected void checkByteFlag() {
		bset = bytes == null ? false : 0 < bytes.length ? true : false;
	}
	
	/** See if a file has been set **/
	protected void checkFileFlag() {
		fset = file != null ? true : false;
	}
	
	/** @return true if byte array is not null **/
	public boolean getByteFlag() {
		return bset;
	}
	
	/** @return true if file is not null **/
	public boolean getFileFlag() {
		return fset;
	}

	/** @return true if file exists on disk; otherwise false **/
	public boolean exists() {
		if(fset) return file.exists();
		else return false;
	}
}
