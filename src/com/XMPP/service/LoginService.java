package com.XMPP.service;

import org.jivesoftware.smack.XMPPConnection;

import com.XMPP.smack.ConnectionHandler;
import com.XMPP.smack.Smack;
import com.XMPP.smack.SmackImpl;
import com.XMPP.util.Constants;
import com.XMPP.util.L;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LoginService extends Service {

	private String server;
	private int port;
	private Smack smack;
	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public LoginService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return LoginService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		smack = new SmackImpl();
		/**i try many times,if the connect operation directly
		 runs in the UI thread ,
		 i always find bug*/
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				smack.connect(server, port);
			}
		}).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		// i can initial some data from the intent arg0, like server name,port
		port = intent.getExtras().getInt("port");
		server = intent.getExtras().getString("server");
		return mBinder;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	/** method for clients */

	public int login(String username, String password) {
		if (smack.getConnection() == null) {
			return Constants.LOGIN_CONNECT_FAIL;
		} else {
			L.i("User request to login:" + username);
			boolean success = smack.login(username, password);
			smack.turnOnlineToAll();
			return success?Constants.LOGIN_SUCCESS:Constants.LOGIN_USERNAME_PSW_ERROR;
		}
	}

}
