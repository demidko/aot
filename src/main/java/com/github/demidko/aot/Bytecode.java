package com.github.demidko.aot;

/**
 * Класс хранит константы байткода
 */
public final class Bytecode {

	public static final byte endOfCompiledLine = 100;

	public static boolean isEndl(byte b) {
		return b == endOfCompiledLine;
	}

	public static boolean isContent(byte b) {
		return b != endOfCompiledLine;
	}
}
