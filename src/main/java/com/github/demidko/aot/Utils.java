package com.github.demidko.aot;

class Utils {

	public static char safeByteToChar(byte b) {
		char c = byteToChar(b);
		if (c == 0) {
			throw new IllegalArgumentException("Invalid byte character: " + b);
		}
		return c;
	}

	public static char byteToChar(byte b) {
		switch (b) {
			case (byte) 0x2d:
				return '-';
			case (byte) 0x96:
				return '–';
			case (byte) 0x97:
				return '—';
			case (byte) 0x30:
				return '0';
			case (byte) 0x31:
				return '1';
			case (byte) 0x32:
				return '2';
			case (byte) 0x33:
				return '3';
			case (byte) 0x34:
				return '4';
			case (byte) 0x35:
				return '5';
			case (byte) 0x36:
				return '6';
			case (byte) 0x37:
				return '7';
			case (byte) 0x38:
				return '8';
			case (byte) 0x39:
				return '9';
			case (byte) 0xa8:
				return 'Ё';
			case (byte) 0xb8:
				return 'ё';
			case (byte) 0xc0:
				return 'А';
			case (byte) 0xc1:
				return 'Б';
			case (byte) 0xc2:
				return 'В';
			case (byte) 0xc3:
				return 'Г';
			case (byte) 0xc4:
				return 'Д';
			case (byte) 0xc5:
				return 'Е';
			case (byte) 0xc6:
				return 'Ж';
			case (byte) 0xc7:
				return 'З';
			case (byte) 0xc8:
				return 'И';
			case (byte) 0xc9:
				return 'Й';
			case (byte) 0xca:
				return 'К';
			case (byte) 0xcb:
				return 'Л';
			case (byte) 0xcc:
				return 'М';
			case (byte) 0xcd:
				return 'Н';
			case (byte) 0xce:
				return 'О';
			case (byte) 0xcf:
				return 'П';
			case (byte) 0xd0:
				return 'Р';
			case (byte) 0xd1:
				return 'С';
			case (byte) 0xd2:
				return 'Т';
			case (byte) 0xd3:
				return 'У';
			case (byte) 0xd4:
				return 'Ф';
			case (byte) 0xd5:
				return 'Х';
			case (byte) 0xd6:
				return 'Ц';
			case (byte) 0xd7:
				return 'Ч';
			case (byte) 0xd8:
				return 'Ш';
			case (byte) 0xd9:
				return 'Щ';
			case (byte) 0xda:
				return 'Ъ';
			case (byte) 0xdb:
				return 'Ы';
			case (byte) 0xdc:
				return 'Ь';
			case (byte) 0xdd:
				return 'Э';
			case (byte) 0xde:
				return 'Ю';
			case (byte) 0xdf:
				return 'Я';
			case (byte) 0xe0:
				return 'а';
			case (byte) 0xe1:
				return 'б';
			case (byte) 0xe2:
				return 'в';
			case (byte) 0xe3:
				return 'г';
			case (byte) 0xe4:
				return 'д';
			case (byte) 0xe5:
				return 'е';
			case (byte) 0xe6:
				return 'ж';
			case (byte) 0xe7:
				return 'з';
			case (byte) 0xe8:
				return 'и';
			case (byte) 0xe9:
				return 'й';
			case (byte) 0xea:
				return 'к';
			case (byte) 0xeb:
				return 'л';
			case (byte) 0xec:
				return 'м';
			case (byte) 0xed:
				return 'н';
			case (byte) 0xee:
				return 'о';
			case (byte) 0xef:
				return 'п';
			case (byte) 0xf0:
				return 'р';
			case (byte) 0xf1:
				return 'с';
			case (byte) 0xf2:
				return 'т';
			case (byte) 0xf3:
				return 'у';
			case (byte) 0xf4:
				return 'ф';
			case (byte) 0xf5:
				return 'х';
			case (byte) 0xf6:
				return 'ц';
			case (byte) 0xf7:
				return 'ч';
			case (byte) 0xf8:
				return 'ш';
			case (byte) 0xf9:
				return 'щ';
			case (byte) 0xfa:
				return 'ъ';
			case (byte) 0xfb:
				return 'ы';
			case (byte) 0xfc:
				return 'ь';
			case (byte) 0xfd:
				return 'э';
			case (byte) 0xfe:
				return 'ю';
			case (byte) 0xff:
				return 'я';

			default:
				return 0;
		}
	}

	public static byte safeCharToByte(char n) {
		byte b = charToByte(n);
		if (b == 0) {
			throw new IllegalArgumentException("Invalid character: " + n);
		}
		return b;
	}

	public static byte charToByte(char n) {
		switch (n) {
			case '-':
				return (byte) 0x2d;
			case '0':
				return (byte) 0x30;
			case '1':
				return (byte) 0x31;
			case '2':
				return (byte) 0x32;
			case '3':
				return (byte) 0x33;
			case '4':
				return (byte) 0x34;
			case '5':
				return (byte) 0x35;
			case '6':
				return (byte) 0x36;
			case '7':
				return (byte) 0x37;
			case '8':
				return (byte) 0x38;
			case '9':
				return (byte) 0x39;
			case '–':
				return (byte) 0x96;
			case '—':
				return (byte) 0x97;
			case 'А':
				return (byte) 0xc0;
			case 'Б':
				return (byte) 0xc1;
			case 'В':
				return (byte) 0xc2;
			case 'Г':
				return (byte) 0xc3;
			case 'Д':
				return (byte) 0xc4;
			case 'Е':
			case 'Ё':
				return (byte) 0xc5;
			case 'Ж':
				return (byte) 0xc6;
			case 'З':
				return (byte) 0xc7;
			case 'И':
				return (byte) 0xc8;
			case 'Й':
				return (byte) 0xc9;
			case 'К':
				return (byte) 0xca;
			case 'Л':
				return (byte) 0xcb;
			case 'М':
				return (byte) 0xcc;
			case 'Н':
				return (byte) 0xcd;
			case 'О':
				return (byte) 0xce;
			case 'П':
				return (byte) 0xcf;
			case 'Р':
				return (byte) 0xd0;
			case 'С':
				return (byte) 0xd1;
			case 'Т':
				return (byte) 0xd2;
			case 'У':
				return (byte) 0xd3;
			case 'Ф':
				return (byte) 0xd4;
			case 'Х':
				return (byte) 0xd5;
			case 'Ц':
				return (byte) 0xd6;
			case 'Ч':
				return (byte) 0xd7;
			case 'Ш':
				return (byte) 0xd8;
			case 'Щ':
				return (byte) 0xd9;
			case 'Ъ':
				return (byte) 0xda;
			case 'Ы':
				return (byte) 0xdb;
			case 'Ь':
				return (byte) 0xdc;
			case 'Э':
				return (byte) 0xdd;
			case 'Ю':
				return (byte) 0xde;
			case 'Я':
				return (byte) 0xdf;
			case 'а':
				return (byte) 0xe0;
			case 'б':
				return (byte) 0xe1;
			case 'в':
				return (byte) 0xe2;
			case 'г':
				return (byte) 0xe3;
			case 'д':
				return (byte) 0xe4;
			case 'е':
			case 'ё':
				return (byte) 0xe5;
			case 'ж':
				return (byte) 0xe6;
			case 'з':
				return (byte) 0xe7;
			case 'и':
				return (byte) 0xe8;
			case 'й':
				return (byte) 0xe9;
			case 'к':
				return (byte) 0xea;
			case 'л':
				return (byte) 0xeb;
			case 'м':
				return (byte) 0xec;
			case 'н':
				return (byte) 0xed;
			case 'о':
				return (byte) 0xee;
			case 'п':
				return (byte) 0xef;
			case 'р':
				return (byte) 0xf0;
			case 'с':
				return (byte) 0xf1;
			case 'т':
				return (byte) 0xf2;
			case 'у':
				return (byte) 0xf3;
			case 'ф':
				return (byte) 0xf4;
			case 'х':
				return (byte) 0xf5;
			case 'ц':
				return (byte) 0xf6;
			case 'ч':
				return (byte) 0xf7;
			case 'ш':
				return (byte) 0xf8;
			case 'щ':
				return (byte) 0xf9;
			case 'ъ':
				return (byte) 0xfa;
			case 'ы':
				return (byte) 0xfb;
			case 'ь':
				return (byte) 0xfc;
			case 'э':
				return (byte) 0xfd;
			case 'ю':
				return (byte) 0xfe;
			case 'я':
				return (byte) 0xff;

			default:
				return 0x0;
		}
	}
}
