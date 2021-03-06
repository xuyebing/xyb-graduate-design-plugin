package graduate_design_plugin;

import graduate_design_plugin.resource.ImageNames;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "graduate_design_plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * preferenceListeners Added by Xu Yebing
	 */
	private Vector<IPreferenceListener> preferenceListeners;
	
	public void addPreferenceListener(IPreferenceListener pPreferenceListener) {
		this.preferenceListeners.add(pPreferenceListener);
	}
	public void preferencesUpdate() {
		for (IPreferenceListener preferenceListener : preferenceListeners) {
			preferenceListener.preferenceChanged();
		}
	}
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.preferenceListeners = new Vector<IPreferenceListener>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * 为项目注册需要使用的图片
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(ImageNames.ICON_START,
				getImageDescriptor("icons/start_button.jpg"));
	}

}
