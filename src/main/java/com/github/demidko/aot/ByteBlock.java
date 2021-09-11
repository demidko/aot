package com.github.demidko.aot;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Блок из байт
 */
class ByteBlock {

	private final int linesCount;
	private final byte[] bytes;

	private ByteBlock(int linesCount, byte[] bytes) {
		this.linesCount = linesCount;
		this.bytes = bytes;
	}

	/**
	 * @return количество наборов байт разделенных \n в блоке
	 */
	int getLinesCount() {
		return linesCount;
	}

	/**
	 * @return байты блока
	 */
	byte[] getBytes() {
		return bytes;
	}

	/**
	 * @param file файл
	 * @return блок байт
	 */
	static ByteBlock readBlockFrom(DataInputStream file) throws IOException {
		int size = file.readInt();
		byte[] bytes = new byte[file.readInt()];
		file.readFully(bytes);
		return new ByteBlock(size, bytes);
	}
}
