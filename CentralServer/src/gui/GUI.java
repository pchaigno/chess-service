package gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import core.Bot;
import core.CentralServerResourceDeployer;
import core.DatabaseManager;
import core.EndingsDatabase;
import core.OpeningsDatabase;
import core.PropertiesManager;
import core.Resource;
import core.ResourcesManager;

/**
 * The graphical user interface to manage resources and control the server.
 * It's also possible to change the settings.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
public class GUI {
	static int WEIGHT_DIGITS = 2;
	private static Display display;
	static Shell shell;
	static Table resourcesTable;

	/**
	 * Main constructor.
	 */
	public static Shell buildInterface() {
		display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Central Server (working db:"+DatabaseManager.getDatabaseFile()+")");
		shell.setImage(new Image(display, "chess.ico"));
		shell.setLayout(new FillLayout());
		buildMenu();
		buildResourcesTable();
		return shell;
	}
	
	/**
	 * Build the menu bar.
	 */
	private static void buildMenu() {
		Menu menu = new Menu(shell, SWT.BAR);
		
		// Menu file:
		MenuItem optionFile = new MenuItem(menu, SWT.CASCADE);
		optionFile.setText("File");
		Menu menuFile = new Menu(shell, SWT.DROP_DOWN);
		optionFile.setMenu(menuFile);
		
		// Option "change database":
		final MenuItem optionChangeDatabase = new MenuItem(menuFile, SWT.PUSH);
		optionChangeDatabase.setText("Change database");
		optionChangeDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dialogDatabase = new InputDialog(Display.getCurrent().getActiveShell(),
						"Change database", "Enter the name of you want to work on", "", null);
				if(dialogDatabase.open() == Window.OK) {
					DatabaseManager.changeDatabase(dialogDatabase.getValue());
					shell.setText("Central Server (working db:"+DatabaseManager.getDatabaseFile()+")");
					resourcesTable.removeAll();
					Set<Resource> resources = ResourcesManager.getResources(false);
					for(Resource resource: resources) {
						addResourceItem(resource);
					}
				}
			}
		});
		
		// Option "Set working database as default":
		final MenuItem optionSetWorkingDbAsDefault = new MenuItem(menuFile, SWT.PUSH);
		optionSetWorkingDbAsDefault.setText("Set working database as default");
		optionSetWorkingDbAsDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesManager.setProperty(PropertiesManager.PROPERTY_DATABASE, DatabaseManager.getDatabaseFile());
				PropertiesManager.saveProperties();
			}
		});
		
		@SuppressWarnings("unused")
		MenuItem databaseSeparator = new MenuItem(menuFile, SWT.SEPARATOR);
		
		// Options start and stop:
		final MenuItem optionStart = new MenuItem(menuFile, SWT.PUSH);
		optionStart.setText("Start server\tCtrl+S");
		optionStart.setAccelerator(SWT.CTRL+'S');
		final MenuItem optionStop = new MenuItem(menuFile, SWT.PUSH);
		optionStop.setText("Stop server\tCtrl+S");
		optionStop.setAccelerator(SWT.CTRL+'S');
		optionStop.setEnabled(false);
		optionStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(CentralServerResourceDeployer.start()) {
					optionStop.setEnabled(true);
					optionStart.setEnabled(false);
				} else {
					MessageDialog.openError(shell, "Server error", "Unable to start the server");
				}
			}
		});
		optionStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(CentralServerResourceDeployer.stop()) {
					optionStop.setEnabled(false);
					optionStart.setEnabled(true);
				} else {
					MessageDialog.openError(shell, "Server error", "Unable to stop the server");
				}
			}
		});
		
		@SuppressWarnings("unused")
		MenuItem optionSeparator = new MenuItem(menuFile, SWT.SEPARATOR);
		
		// Option "close":
		MenuItem optionClose = new MenuItem(menuFile, SWT.PUSH);
		optionClose.setText("Exit");
		optionClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		// Menu "options":
		MenuItem optionOptions = new MenuItem(menu, SWT.CASCADE);
		Menu menuOptions = new Menu(shell, SWT.DROP_DOWN);
		optionOptions.setMenu(menuOptions);
		optionOptions.setText("Options");
		
		// Option "SetOptions"
		MenuItem optionSetOptions = new MenuItem(menuOptions, SWT.CASCADE);
		optionSetOptions.setText("Set options");
		optionSetOptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildOptionsShell();
			}
		});
		
		// Menu help:
		MenuItem optionHelp = new MenuItem(menu, SWT.CASCADE);
		Menu menuHelp = new Menu(shell, SWT.DROP_DOWN);
		optionHelp.setMenu(menuHelp);
		optionHelp.setText("Help");
		
		// Option "about":
		MenuItem optionAbout = new MenuItem(menuHelp, SWT.CASCADE);
		optionAbout.setText("About");
		optionAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell aPropos = new Shell(shell, SWT.TITLE | SWT.CLOSE);
				aPropos.setText("A propos");
				aPropos.setLayout(new FillLayout());
				aPropos.setSize(200, 200);
		        
				Label aProposText = new Label(aPropos, SWT.NONE);
				aProposText.setText("Central Server");
				
				aPropos.open();
			}
		});
		  
		shell.setMenuBar(menu);
	}
	
	/**
	 * Build the option shell.
	 * The window allow the user to change the settings.
	 */
	static void buildOptionsShell() {
		final Shell shell = new Shell();
		shell.setText("Options");
		shell.setImage(new Image(display, "chess.ico"));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		shell.setLayout(gridLayout);
		
		// Port field:
		Label portLabel = new Label(shell, SWT.NONE);
		portLabel.setText("Listening port: ");
		final Spinner port = new Spinner(shell, SWT.BORDER);
		port.setIncrement(1);
		port.setMinimum(0);
		port.setMaximum(Integer.MAX_VALUE);
		port.setSelection(Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_PORT_LISTENER)));
		port.setLayoutData(new GridData(150, 13));
		
		// Connect timeout field:
		Label connectTimeoutLabel = new Label(shell, SWT.NONE);
		connectTimeoutLabel.setText("Connect Timeout(ms): ");
		final Spinner connectTimeout = new Spinner(shell, SWT.BORDER);
		connectTimeout.setIncrement(100);
		connectTimeout.setMinimum(0);
		connectTimeout.setMaximum(Integer.MAX_VALUE);
		connectTimeout.setSelection(Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_CONNECT_TIMEOUT)));
		connectTimeout.setLayoutData(new GridData(150, 13));
		
		// Read timeout field:
		Label readTimeoutLabel = new Label(shell, SWT.NONE);
		readTimeoutLabel.setText("Read Timeout(ms): ");
		final Spinner readTimeout = new Spinner(shell, SWT.BORDER);
		readTimeout.setIncrement(100);
		readTimeout.setMinimum(0);
		readTimeout.setMaximum(Integer.MAX_VALUE);
		readTimeout.setSelection(Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_READ_TIMEOUT)));
		readTimeout.setLayoutData(new GridData(150, 13));
		
		// Database field:
		Label databaseLabel = new Label(shell, SWT.NONE);
		databaseLabel.setText("Database used by central server: ");
		final Text database = new Text(shell, SWT.BORDER);
		database.setText(PropertiesManager.getProperty(PropertiesManager.PROPERTY_DATABASE));
		database.setLayoutData(new GridData(300, 13));
		
		Label separatorOpenings1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separatorOpenings1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label separatorOpenings2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separatorOpenings2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Weight nbPlay field
		Label weightNbPlayLabel = new Label(shell, SWT.NONE);
		weightNbPlayLabel.setText("Weight for number of play: ");
		final Spinner weightNbPlay = new Spinner(shell, SWT.BORDER);
		weightNbPlay.setDigits(WEIGHT_DIGITS);
		weightNbPlay.setIncrement(10);
		weightNbPlay.setMinimum(0);
		weightNbPlay.setMaximum((int)Math.pow(10, WEIGHT_DIGITS));
		weightNbPlay.setSelection((int)(Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_NBPLAY))*Math.pow(10,WEIGHT_DIGITS)));
		weightNbPlay.setLayoutData(new GridData(150, 13));
		
		// Weight probaW field
		Label weightProbaWLabel = new Label(shell, SWT.NONE);
		weightProbaWLabel.setText("Weight for proba of winning: ");
		final Spinner weightProbaW = new Spinner(shell, SWT.BORDER);
		weightProbaW.setDigits(WEIGHT_DIGITS);
		weightProbaW.setIncrement(10);
		weightProbaW.setMinimum(0);
		weightProbaW.setMaximum((int)Math.pow(10, WEIGHT_DIGITS));
		weightProbaW.setSelection((int)(Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_PROBAW))*Math.pow(10,WEIGHT_DIGITS)));
		weightProbaW.setLayoutData(new GridData(150, 13));
		
		Label separatorBots1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separatorBots1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label separatorBots2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separatorBots2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Weight engineScore field
		Label weightEngineScoreLabel = new Label(shell, SWT.NONE);
		weightEngineScoreLabel.setText("Weight for engine score: ");
		final Spinner weightEngineScore = new Spinner(shell, SWT.BORDER);
		weightEngineScore.setDigits(WEIGHT_DIGITS);
		weightEngineScore.setIncrement(10);
		weightEngineScore.setMinimum(0);
		weightEngineScore.setMaximum((int)Math.pow(10, WEIGHT_DIGITS));
		weightEngineScore.setSelection((int)(Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_ENGINESCORE))*Math.pow(10,WEIGHT_DIGITS)));
		weightEngineScore.setLayoutData(new GridData(150, 13));

		// Weight depth field
		Label weightDepthLabel = new Label(shell, SWT.NONE);
		weightDepthLabel.setText("Weight for proba of winning: ");
		final Spinner weightDepth = new Spinner(shell, SWT.BORDER);
		weightDepth.setDigits(WEIGHT_DIGITS);
		weightDepth.setIncrement(10);
		weightDepth.setMinimum(0);
		weightDepth.setMaximum((int)Math.pow(10, WEIGHT_DIGITS));
		weightDepth.setSelection((int)(Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_DEPTH))*Math.pow(10,WEIGHT_DIGITS)));
		weightDepth.setLayoutData(new GridData(150, 13));
		
		// Button submit:
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Submit");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Integer.parseInt(port.getText());
					Integer.parseInt(connectTimeout.getText());
					Integer.parseInt(readTimeout.getText());
					int valueWeightNbPlay = weightNbPlay.getSelection();
					int valueWeightProbaW = weightProbaW.getSelection();
					int valueWeightEngineScore = weightEngineScore.getSelection();
					int valueweightDepth = weightDepth.getSelection();
					
					if(valueWeightNbPlay+valueWeightProbaW!=Math.pow(10, WEIGHT_DIGITS) || valueWeightEngineScore+valueweightDepth!=Math.pow(10, WEIGHT_DIGITS)) {
						MessageDialog.openError(shell, "Weights error", "Sum of weights should equals to 1");
					} else {
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_PORT_LISTENER, port.getText());
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_CONNECT_TIMEOUT, connectTimeout.getText());
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_READ_TIMEOUT, readTimeout.getText());
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_DATABASE, database.getText());
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_WEIGHT_NBPLAY, ""+valueWeightNbPlay/Math.pow(10, WEIGHT_DIGITS));
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_WEIGHT_PROBAW, ""+valueWeightProbaW/Math.pow(10, WEIGHT_DIGITS));
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_WEIGHT_ENGINESCORE, ""+valueWeightEngineScore/Math.pow(10, WEIGHT_DIGITS));
						PropertiesManager.setProperty(PropertiesManager.PROPERTY_WEIGHT_DEPTH, ""+valueweightDepth/Math.pow(10, WEIGHT_DIGITS));
						if(!PropertiesManager.saveProperties()) {
							MessageDialog.openError(shell, "Saving error", "Unable to save the new options.");
						}

						shell.dispose();
					}
				} catch(NumberFormatException e1) {
					MessageDialog.openError(shell, "Format error", "Port, connect timeout, read timeout and weights should be integers");
				}
			}
		});
		shell.setDefaultButton(submit);
		
		shell.pack();
		shell.open();
	}
	
	/**
	 * Build the resources table, the main component.
	 * This resources table contains all the resources and allow the user
	 * to quickly remove, edit or add a resource.
	 */
	private static void buildResourcesTable() {
		// Configuration and headers:
		final TableViewer tableViewer = new TableViewer(shell, SWT.BORDER|SWT.FULL_SELECTION|SWT.MULTI);
		resourcesTable = tableViewer.getTable();
		TableColumn columnType = new TableColumn(resourcesTable, SWT.LEFT);
		columnType.setText("Type");
		columnType.setWidth(100);
		TableColumn columnName = new TableColumn(resourcesTable, SWT.LEFT);
		columnName.setText("Name");
		columnName.setWidth(100);
		TableColumn columnURI = new TableColumn(resourcesTable, SWT.LEFT);
		columnURI.setText("URI");
		columnURI.setWidth(100);
		TableColumn columnTrust = new TableColumn(resourcesTable, SWT.LEFT);
		columnTrust.setText("Trust");
		columnTrust.setWidth(50);
		TableColumn columnActive = new TableColumn(resourcesTable, SWT.LEFT);
		columnActive.setText("Active");
		columnActive.setWidth(50);
		resourcesTable.setHeaderVisible(true);
		resourcesTable.setLinesVisible(true);
		
		buildContextMenu();
		
		// Double click support to the resources table:
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				IStructuredSelection select = (IStructuredSelection)e.getSelection();
				buildEditShell((Resource)select.getFirstElement());
			}
		});
		
		// Listener on delete key for the resources table:
		resourcesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.DEL) {
					removeResources();
				}
			}
		});
		
		// Initialization of the table with resources:
		Set<Resource> resources = ResourcesManager.getResources(false);
		for(Resource resource: resources) {
			addResourceItem(resource);
		}
	}
	
	/**
	 * Add an item representing a resource to the resources table.
	 * @param resource The new resource to add.
	 */
	static void addResourceItem(Resource resource) {
		TableItem resourceItem = new TableItem(resourcesTable, SWT.NONE);
		String type = "Bot";
		if(resource.getClass()==OpeningsDatabase.class) {
			type = "Openings Database";
		} else if(resource.getClass()==EndingsDatabase.class) {
			type = "Endings Database";
		}
		String trust = String.valueOf(resource.getTrust());
		String active = resource.isActive()? "enabled" : "disabled";
		resourceItem.setText(new String[] {type, resource.getName(), resource.getURI(), trust, active});
		resourceItem.setData(resource);
	}
	
	/**
	 * Remove an item representing a resource from the resources table.
	 * @param resource The resource to remove.
	 */
	static void removeResourceItem(Resource resource) {
		TableItem[] items = resourcesTable.getItems();
		for(TableItem item: items) {
			if(resource.equals(item.getData())) {
				item.dispose();
			}
		}
	}
	
	/**
	 * Build the contextual menu for the resources table.
	 * @param resourcesTable The resources table.
	 */
	private static void buildContextMenu() {
		Menu contextMenu = new Menu(resourcesTable);
		
		// Option to enable a resource:
		MenuItem optionEnable = new MenuItem(contextMenu, SWT.PUSH);
		optionEnable.setText("Enable");
		optionEnable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableResources();
			}
		});
		
		// Option to disable a resource:
		MenuItem optionDisable = new MenuItem(contextMenu, SWT.PUSH);
		optionDisable.setText("Disable");
		optionDisable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				disableResources();
			}
		});
		
		// Option to add a resource:
		MenuItem optionAdd = new MenuItem(contextMenu, SWT.PUSH);
		optionAdd.setText("Add");
		optionAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildAddShell();
			}
		});
		
		// Option to edit a resource:
		MenuItem optionEdit = new MenuItem(contextMenu, SWT.PUSH);
		optionEdit.setText("Edit");
		optionEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] resourceItems = resourcesTable.getSelection();
				if(resourceItems.length==1) {
					buildEditShell((Resource)resourceItems[0].getData());
				} else {
					MessageDialog.openWarning(shell, "Action impossible", "You can only edit one resource at the time.");
				}
			}
		});
		
		// Option to remove a resource:
		MenuItem optionRemove = new MenuItem(contextMenu, SWT.PUSH);
		optionRemove.setText("Remove");
		optionRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeResources();
			}
		});
		
		// Bind the contextual menu to the resources table.
		resourcesTable.setMenu(contextMenu);
	}
	
	/**
	 * Enable the resources selected.
	 * Update the resources table.
	 */
	static void enableResources() {
		TableItem[] resourceItems = resourcesTable.getSelection();
		Set<Resource> resources = new HashSet<Resource>();
		for(TableItem resourceItem: resourceItems) {
			Resource resource = (Resource)resourceItem.getData();
			resourceItem.setText(4, "enabled");
			resource.enable();
			resources.add(resource);
		}
		ResourcesManager.updateResourcesActive(resources);
	}
	
	/**
	 * Disable the resources selected.
	 * Update the resources table.
	 */
	static void disableResources() {
		TableItem[] resourceItems = resourcesTable.getSelection();
		Set<Resource> resources = new HashSet<Resource>();
		for(TableItem resourceItem: resourceItems) {
			Resource resource = (Resource)resourceItem.getData();
			resourceItem.setText(4, "disabled");
			resource.disable();
			resources.add(resource);
		}
		ResourcesManager.updateResourcesActive(resources);
	}
	
	/**
	 * Remove the resources selected in the resources table after
	 * a quick confirmation from the user.
	 * Update the resources table.
	 */
	static void removeResources() {
		TableItem[] resourceItems = resourcesTable.getSelection();
		if(resourceItems.length>0) {
			String message = resourceItems.length>1? "Do you really want to remove those resources?" : "Do you really want to remove this resource?";
			boolean confirm = MessageDialog.openConfirm(shell, "Remove resources", message);
	        if(confirm) {
	        	// Get the selected resources:
				Set<Resource> resources = new HashSet<Resource>();
				for(TableItem resourceItem: resourceItems) {
					resources.add((Resource)resourceItem.getData());
				}
				
				// Remove the resources from the database.
				Set<Resource> notRemoved = ResourcesManager.removeResources(resources);
				
				// Remove the resources correctly removed from the database from the table:
				for(TableItem resourceItem: resourceItems) {
					if(!notRemoved.contains(resourceItem.getData())) {
						resourceItem.dispose();
					}
				}
	        }
		}
	}
	
	/**
	 * Build the window to add a new resource.
	 * Simply a small form.
	 */
	static void buildAddShell() {
		// Configuration of the window:
		final Shell shell = new Shell();
		shell.setText("Add a new resource");
		shell.setImage(new Image(display, "chess.ico"));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		shell.setLayout(gridLayout);

		// Type field:
		Label typeLabel = new Label(shell, SWT.NONE);
		typeLabel.setText("Type: ");
		final Combo type = new Combo(shell, SWT.READ_ONLY);
		type.add("Openings Database");
		type.add("Endings Database");
		type.add("Bot");
		type.setLayoutData(new GridData(100, 13));
		
		// Name field:
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name: ");
		final Text name = new Text(shell, SWT.BORDER);
		name.setLayoutData(new GridData(100, 13));

		// URI field:
		Label uriLabel = new Label(shell, SWT.NONE);
		uriLabel.setText("URI: ");
		final Text uri = new Text(shell, SWT.BORDER);
		uri.setLayoutData(new GridData(100, 13));
		
		// Button submit:
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Add");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Resource resource = null;
				if(type.getText().equals("Openings Database")) {
					resource = new OpeningsDatabase(uri.getText(), name.getText(), true, -1);
				} else if(type.getText().equals("Endings Database")) {
					resource = new EndingsDatabase(uri.getText(), name.getText(), true, -1);
				} else {
					resource = new Bot(uri.getText(), name.getText(), true, -1);
				}
				resource = ResourcesManager.addResource(resource);
				if(resource!=null) {
					addResourceItem(resource);
				}
				shell.dispose();
			}
		});
		shell.setDefaultButton(submit);
		
		shell.pack();
		shell.open();
	}
	
	/**
	 * Build the edition window.
	 * Simply the same form than in the add window but with the information.
	 * The URI field can't be changed.
	 * @param resource The resource to be editing.
	 */
	static void buildEditShell(final Resource resource) {
		// Configuration of the window:
		final Shell shell = new Shell();
		shell.setText("Edit "+resource.getName());
		shell.setImage(new Image(display, "chess.ico"));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		shell.setLayout(gridLayout);

		// Type field:
		Label typeLabel = new Label(shell, SWT.NONE);
		typeLabel.setText("Type: ");
		final Combo type = new Combo(shell, SWT.READ_ONLY);
		type.add("Openings Database");
		type.add("Endings Database");
		type.add("Bot");
		String typeText = "Bot";
		if(resource.getClass()==OpeningsDatabase.class) {
			typeText = "Openings Database";
		} else if(resource.getClass()==EndingsDatabase.class) {
			typeText = "Endings Database";
		}
		type.setText(typeText);
		type.setLayoutData(new GridData(100, 13));
		
		// Name field:
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name: ");
		final Text name = new Text(shell, SWT.BORDER);
		name.setText(resource.getName());
		name.setLayoutData(new GridData(100, 13));

		// URI field:
		Label uriLabel = new Label(shell, SWT.NONE);
		uriLabel.setText("URI: ");
		final Text uri = new Text(shell, SWT.BORDER);
		uri.setText(resource.getURI());
		uri.setLayoutData(new GridData(100, 13));
		
		// Button submit:
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Update");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Resource newResource = null;
				if(type.getText().equals("Openings Database")) {
					newResource = new OpeningsDatabase(uri.getText(), name.getText(), true, resource.getId());
				} else if(type.getText().equals("Endings Database")) {
					newResource = new EndingsDatabase(uri.getText(), name.getText(), true, resource.getId());
				} else {
					newResource = new Bot(uri.getText(), name.getText(), true, resource.getId());
				}
				if(ResourcesManager.updateResource(newResource)) {
					removeResourceItem(resource);
					addResourceItem(newResource);
				}
				shell.dispose();
			}
		});
		shell.setDefaultButton(submit);

		shell.pack();
		shell.open();
	}

	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		buildInterface();
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}