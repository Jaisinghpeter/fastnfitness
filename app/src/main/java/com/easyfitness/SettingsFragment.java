package com.easyfitness;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.util.ListenableList;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SettingsFragment extends PreferenceFragmentCompat {

    Toolbar top_toolbar = null;
    MainActivity mActivity = null;
    public MapView mMapView;
    public final static String WEIGHT_UNIT_PARAM =  "defaultUnit";
    public final static String DISTANCE_UNIT_PARAM =  "defaultDistanceUnit";


    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static SettingsFragment newInstance(String name, int id) {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // *** ADD ***

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String param) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_machine, container, false);
        mMapView = view.findViewById(R.id.mapView);
        setupLocationDisplay();
        setupMap();
        return view;
    }

    private void updateSummary(ListPreference pref, String val, String prefix) {
        int prefIndex = pref.findIndexOfValue(val);
        if (prefIndex >= 0) {
            //finally set's it value changed
            pref.setSummary(prefix + pref.getEntries()[prefIndex]);
        }
    }
//    private void setupMap() {
//        if (mMapView != null) {
//            ArcGISRuntimeEnvironment.setLicense(getResources().getString(R.string.arcgis_license_key));
//            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
//
//
//            double latitude = 13.0827;
//            double longitude = 80.2707;
//            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CALENDAR)
//                == PackageManager.PERMISSION_GRANTED) {
//                // Permission is not granted
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
//
//            }
//            int levelOfDetail = 13;
//            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
//            mMapView.setMap(map);
//        }
//    }


    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }
    private GraphicsOverlay graphicsOverlay;
    private LocatorTask locator = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private Spinner spinner;
    private LocationDisplay mLocationDisplay;
    // setup a new map at your area of interest.
    public void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            double latitude = 34.09042;
            double longitude = -118.71511;
            int levelOfDetail = 10;
            final ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);

            // wait for the map to be fully ready and rendered before allowing a place search
            mMapView.addViewpointChangedListener(new ViewpointChangedListener() {
                @Override
                public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {
                    if (graphicsOverlay == null) {
                        // Create a graphics overlay that will show the places found from a search.
                        graphicsOverlay = new GraphicsOverlay();
                        mMapView.getGraphicsOverlays().add(graphicsOverlay);
                        setupSpinner();
                        setupPlaceTouchListener();
                        setupNavigationChangedListener();
                        mMapView.removeViewpointChangedListener(this);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {

            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
//            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK or no error is reported, then continue.
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            if (!(ContextCompat.checkSelfPermission(mActivity, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {

                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(mActivity, requestPermissions, requestPermissionsCode);
            } else {

                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                    .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
            }
        });
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.startAsync();
    }


    // The Spinner shows the possible categories to search for. When the user selects a
    // category call findPlaces with the category.
    private void setupSpinner() {
        spinner = getView().findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                findPlaces(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        findPlaces(spinner.getSelectedItem().toString());
    }

    // Provide additional information about a specific place if the user taps that graphic.
    private void setupPlaceTouchListener() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {

                // Dismiss a prior callout.
                mMapView.getCallout().dismiss();

                // get the screen point where user tapped
                final android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY()));

                // identify graphics on the graphics overlay
                final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(graphicsOverlay, screenPoint, 10.0, false, 2);

                identifyGraphic.addDoneListener(() -> {
                    try {
                        IdentifyGraphicsOverlayResult graphicsResult = identifyGraphic.get();
                        // get the list of graphics returned by identify graphic overlay
                        List<Graphic> graphicList = graphicsResult.getGraphics();

                        // get the first graphic selected and show its attributes with a callout
                        if (!graphicList.isEmpty()){
                            showCalloutAtLocation(graphicList.get(0), mMapView.screenToLocation(screenPoint));
                        }
                    } catch (InterruptedException | ExecutionException exception) {
                        exception.printStackTrace();
                    }
                });
                return super.onSingleTapConfirmed(motionEvent);
            }
        });
    }

    private void setupNavigationChangedListener() {
        mMapView.addNavigationChangedListener(navigationChangedEvent -> {
            if (!navigationChangedEvent.isNavigating()) {
                // Dismiss a prior callout before doing a new search.
                mMapView.getCallout().dismiss();
                findPlaces(spinner.getSelectedItem().toString());
            }
        });
    }

    // Show the selected graphic with its attributes in a callout
    private void showCalloutAtLocation(Graphic graphic, Point mapPoint) {
        Callout callout = mMapView.getCallout();
        TextView calloutContent = new TextView(getActivity());

        callout.setLocation(graphic.computeCalloutLocation(mapPoint, mMapView));
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setText(Html.fromHtml("<b>" + graphic.getAttributes().get("PlaceName").toString() + "</b><br>" + graphic.getAttributes().get("Place_addr").toString()));
        callout.setContent(calloutContent);
        callout.show();
    }

    // Find places of the requested category within 50 kilometers of the center of the current map view.
    private void findPlaces(String placeCategory) {
        GeocodeParameters parameters = new GeocodeParameters();
        Point searchPoint;

        // Set the current map extent as the current location. The search is limited to a
        // radius of 50 kilometers around this point.
        if (mMapView.getVisibleArea() != null) {
            searchPoint = mMapView.getVisibleArea().getExtent().getCenter();
            if (searchPoint == null) {
                return;
            }
        } else {
            return;
        }
        parameters.setPreferredSearchLocation(searchPoint);

        // We're interested in the top 25 nearest places.
        parameters.setMaxResults(25);

        // Return the place name and address fields in the results.
        List<String> outputAttributes = parameters.getResultAttributeNames();
        outputAttributes.add("Place_addr");
        outputAttributes.add("PlaceName");

        // Execute the search and add the places to the graphics overlay.
        final ListenableFuture<List<GeocodeResult>> results = locator.geocodeAsync(placeCategory, parameters);
        results.addDoneListener(() -> {
            try {
                ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
                graphics.clear();
                List<GeocodeResult> places = results.get();
                for (GeocodeResult result : places) {

                    // Add a graphic representing each location with a simple marker symbol.
                    SimpleMarkerSymbol placeSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 10);
                    placeSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2));
                    Graphic graphic = new Graphic(result.getDisplayLocation(), placeSymbol);
                    java.util.Map<String, Object> attributes = result.getAttributes();

                    // Store the location attributes with the graphic for later recall when this location is identified.
                    for (String key : attributes.keySet()) {
                        String value = attributes.get(key).toString();
                        graphic.getAttributes().put(key, value);
                    }
                    graphics.add(graphic);
                }
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }
        });
    }

}
