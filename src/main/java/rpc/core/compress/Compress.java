package rpc.core.compress;

import rpc.core.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] in);
    byte[] decompress(byte[] in);
}
