package com.example.musicplayer.audiotrack;

public class MusicDecoder implements IMusicDecoder{
    @Override
    public int getMusicMetaByPath(String accompanyPath, int[] metaArray) {
        return getMusicMeta(accompanyPath, metaArray);
    }
    @Override
    public void init(String accompanyPath, float packetBufferTimePercent) {
        openFile(accompanyPath, packetBufferTimePercent);
    }
    @Override
    public int readSamples(short[] samples, int[] slientSizeArr) {
        return readSamples(samples, samples.length, slientSizeArr);
    }
    @Override
    public void destory() {
        closeFile();
    }

    /** 1、获取伴奏文件的meta信息，一个是采样率一个是比特率 **/
    private native int getMusicMeta(String musicPath, int[] metaArray);
    /** 2、init，其实是打开伴奏文件 **/
    private native int openFile(String accompanyPath, float packetBufferTimePercent);
    /** 3、打开文件之后读取samples处理 **/
    private native int readSamples(short[] samples, int size, int[] slientSizeArr);
    /** 4、 最终当歌曲结束时，关闭文件  **/
    private native void closeFile();
}
