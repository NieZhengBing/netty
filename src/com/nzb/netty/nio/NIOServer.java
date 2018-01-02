package com.nzb.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer {
	
//	channel socket manage
	private Selector selector;
	
	/**
	 * start End-to-End testing
	 * @param 
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		NIOServer server = new NIOServer();
		server.initServer(8000);
	}

	/**
	 * get a ServerSocket Channel, and do some init
	 * @param port 
	 * @throws IOException 
	 */
	public void initServer(int port) throws IOException {
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.socket().bind(new InetSocketAddress(port));
		this.selector = Selector.open();
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * poll listening to event selector, if have then do it 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		System.out.println("server start success!");
		while(true) {
			selector.select();
			Iterator<?> ite = this.selector.selectedKeys().iterator();
			while(ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				ite.remove();
				
				handler(key);
			}
		}
	}

	/**
	 * do request
	 * @param key
	 * @throws IOException 
	 */
	public void handler(SelectionKey key) throws IOException {
		if (key.isAcceptable()) {
			handleAccept(key);
		} else if (key.isReadable()) {
			handleKeyRead(key);
		}
	}

	/**
	 * do with read event
	 * @param key
	 * @throws IOException
	 */
	public void handleKeyRead(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel =  server.accept();
		channel.configureBlocking(false);
		System.out.println("new client connect");
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	/***
	 * do with write event
	 * @param key
	 * @throws IOException
	 */
	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int read = channel.read(buffer);
		if (read > 0) {
			byte[] data = buffer.array();
			String message = new String(data).trim();
			System.out.println("server receive the message " + message);
			ByteBuffer outBuffer = ByteBuffer.wrap("ok".getBytes());
			channel.write(outBuffer);
		} else {
			System.out.println("client close");
			key.channel();
		}
	}
	
 
}
