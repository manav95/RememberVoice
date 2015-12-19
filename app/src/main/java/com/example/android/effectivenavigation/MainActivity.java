/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.effectivenavigation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import 	android.widget.ArrayAdapter;
import it.bova.rtmapi.*;
import android.app.ActionBar;
import android.app.Activity;
import android.widget.Toast;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.widget.EditText;
import it.bova.rtmapi.RtmApi;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import android.widget.Spinner;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	protected static final int REQUEST_OK = 1;
	protected static Activity thisActivity;
	protected static Token authToken;
    protected static String fileName = "authToken.txt";
	protected static String textBox;
    protected static String spokenMessage;
    protected static List<TaskList> tasks;
	public UserHolder holder;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    public UserHolder getHolder() {
    	return holder;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileInputStream fis = getBaseContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            authToken = (Token) is.readObject();
            is.close();
            fis.close();
        }
        catch(FileNotFoundException e) {
            Toast.makeText(getBaseContext(), (CharSequence)"No auth token previously found.", Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            Log.e("ERROR:", e.getStackTrace().toString());
        }
        catch (ClassNotFoundException e){
            Log.e("ERROR:", e.getStackTrace().toString());
        }
        holder = new UserHolder();

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Section" + (i + 1))
                            .setTabListener(this));
        }
        thisActivity = this;
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	   super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new LaunchpadSectionFragment();
                case 1:
                	return new GoogleVoiceFragment();
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment implements OnClickListener{
       protected RtmApiAuthenticator authenticator;
       protected String frob;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);
            rootView.findViewById(R.id.buttoneer).setOnClickListener(this);
            return rootView;
        }
        public void onClick(View view) {
        	authenticator = new RtmApiAuthenticator("825cc1d0232ac2c1fae4a1db178c7641","b9872fada43720d6");
			new MainMethod().execute(authenticator);
        }
        private class MainMethod extends AsyncTask<RtmApiAuthenticator, Void, Void> {
		@Override
		protected Void doInBackground(RtmApiAuthenticator... params) {
			try {
				frob = authenticator.authGetFrob();
				String validationUrl = authenticator.authGetDesktopUrl(Permission.DELETE,frob);
				 ConnectivityManager connMgr = (ConnectivityManager) 
				            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				        if (networkInfo != null && networkInfo.isConnected()) {
				        	 Uri uri = Uri.parse(validationUrl);
				        	 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				        	 startActivityForResult(intent, 2);
				        } else {
				            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_LONG);
				        }
			}
			 catch (ServerException e) {
					// TODO Auto-generated catch block
					Log.e("ERROR:", e.getStackTrace().toString());
				} catch (RtmApiException e) {
					// TODO Auto-generated catch block
					Log.e("ERROR:", e.getStackTrace().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("ERROR:", e.getStackTrace().toString());
				}
			return null;
         }
		}
        private class ConnectorThread extends AsyncTask<RtmApiAuthenticator, Void, Void> {
        	@Override
    		protected Void doInBackground(RtmApiAuthenticator... params) {
        		try {
			        authToken = authenticator.authGetToken(frob);
                    FileOutputStream fos = LaunchpadSectionFragment.this.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(authToken);
                    os.close();
                    fos.close();
				}
	             catch (ServerException e) {
		              // TODO Auto-generated catch block
	                 e.printStackTrace();
	              } catch (RtmApiException e) {
		               // TODO Auto-generated catch block
	               	e.printStackTrace();
	              } catch (IOException e) {
	            	// TODO Auto-generated catch block
		            e.printStackTrace();
	             }
				return null;
	            }
    	}	
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
			new ConnectorThread().execute(authenticator);
	 }
    }
    public static class GoogleVoiceFragment extends Fragment implements OnClickListener{
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		 View rootView = inflater.inflate(R.layout.fragment_section_voice, container, false);
    		 rootView.findViewById(R.id.typedInfo).setOnClickListener(this);
    		 rootView.findViewById(R.id.voiceOne).setOnClickListener(new OnClickListener() {
    			 public void onClick(View view) {
    				  Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    			         i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
    		        	 try {
    		                 startActivityForResult(i, 111);
    		             } 
    		        	 catch (Exception e) {
    		        	 	Toast.makeText(view.getContext(), (CharSequence)"Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
    		         }
    			 }
    		     });
			return rootView;  
        }
    	public void onClick(View v) {
    		EditText field = (EditText)getActivity().findViewById(R.id.textViewEight);
    		Editable text = field.getText();
    		spokenMessage = text.toString();
    	}
    	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        		spokenMessage = thingsYouSaid.get(0);
	        		((TextView)getActivity().findViewById(R.id.textMaster)).setText(thingsYouSaid.get(0));
    	 }
    }
    public static class DummySectionFragment extends Fragment implements AdapterView.OnItemSelectedListener{

        public static final String ARG_SECTION_NUMBER = "section_number";
        private static String textBox;
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            textBox = (String) parent.getItemAtPosition(pos);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            RtmApi api = new RtmApi("825cc1d0232ac2c1fae4a1db178c7641","b9872fada43720d6",authToken);
            try {
                tasks = api.listsGetList();
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (RtmApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Spinner spinner = (Spinner) rootView.findViewById(R.id.listSpinner);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(rootView.getContext(),android.R.layout.simple_spinner_item, tasks);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            Bundle args = getArguments();
            rootView.findViewById(R.id.buttonFour).setOnClickListener(new OnClickListener() {
            	 public void onClick(View view) {
                       RtmApi api = new RtmApi("825cc1d0232ac2c1fae4a1db178c7641","b9872fada43720d6",authToken);
            		   new AddingTask().execute(api);
   			 }
   		     });
            return rootView;
        }
        private class AddingTask extends AsyncTask<RtmApi, Void, Void> {
        	protected Void doInBackground(RtmApi... apis) {
        		try {
        			RtmApi apiMain = apis[0];
        			TaskList task = null;
        			for (TaskList tk : tasks) {
        				if (tk.getName().equals(textBox)) {
        					task = tk;
        					break;
        				}
        			}
					apiMain.tasksAddSmartly(apis[0].timelinesCreate(), spokenMessage, task);
				} catch (ServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RtmApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
        	}
        }
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
    		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    		((TextView)getActivity().findViewById(R.id.textMaster)).setText(thingsYouSaid.get(0));
        }
    }
}
