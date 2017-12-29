package com.nzb.netty.oio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OioServer {
	public static void main(String[] args) throws IOException {
		ExecutorService newCacheThreadPool = Executors.newCachedThreadPool();
		
		ServerSocket server = new ServerSocket(10101);
        System.out.println("server start");		
        
        while(true) {
        	final Socket socket = server.accept();
        	System.out.println("a new client");
        	newCacheThreadPool.execute(new Runnable () {

				@Override
				public void run() {
//					business process
					handler(socket);
				}
        	});
        }
	}
	
	/**
	 * read data
	 * @param socket
	 */
	private static void handler(Socket socket) {
		try {
			byte[] bytes = new byte[1024];
			InputStream inputStream = socket.getInputStream();
			
			while(true) {
//				read data(block)
				int read = inputStream.read(bytes);
				if (read != -1) {
					System.out.println(new String(bytes, 0, read));
				} else {
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("socket closed");
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
