package zad1;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {


    public static void processDir(String dirName, String resultFileName) {



        try {

            Path destFilePath = Paths.get(System.getProperty("user.dir")+FileSystems.getDefault().getSeparator()+resultFileName);

            Files.deleteIfExists(destFilePath);
            FileChannel destFile =  FileChannel.open(destFilePath,StandardOpenOption.CREATE_NEW,StandardOpenOption.APPEND);

            Files.walkFileTree(Paths.get(dirName), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    try(FileChannel fileChannelSrc = (FileChannel) Files.newByteChannel(file)){ //autoClosedChannel

                        long fileSize = fileChannelSrc.size();//size of file
                        MappedByteBuffer mappedByteBuffer = fileChannelSrc.map(FileChannel.MapMode.READ_ONLY,0,fileSize);
                        CharBuffer charBuffer = Charset.forName("cp1250").decode(mappedByteBuffer);

                        destFile.write(Charset.forName("UTF8").encode(charBuffer));

                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            destFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
