package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import core.Bot;
import core.Database;
import core.Resource;
import core.ResourcesManager;

/**
 * The graphical user interface to manage resources and control the server.
 */
public class GUI {
	private static Shell shell;
	private static Table resourcesTable;

	/**
	 * Main constructor.
	 * @param shell The principal shell.
	 */
	public static void buildInterface(Shell shell) {
		GUI.shell = shell;
		buildMenu();
		buildResourcesTable();
	}
	
	/**
	 * Build the menu bar.
	 */
	private static void buildMenu() {
		Menu menu = new Menu(shell, SWT.BAR);
		MenuItem optionFile = new MenuItem(menu, SWT.CASCADE);
		optionFile.setText("File");
		Menu menuFile = new Menu(shell, SWT.DROP_DOWN);
		optionFile.setMenu(menuFile);
		final MenuItem optionStart = new MenuItem(menuFile, SWT.PUSH);
		optionStart.setText("Start server");
		final MenuItem optionStop = new MenuItem(menuFile, SWT.PUSH);
		optionStop.setText("Stop server");
		optionStop.setEnabled(false);
		optionStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Start the server.
				optionStop.setEnabled(true);
				optionStart.setEnabled(false);
			}
		});
		optionStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Stop the server.
				optionStop.setEnabled(false);
				optionStart.setEnabled(true);
			}
		});
		@SuppressWarnings("unused")
		MenuItem optionSeparator = new MenuItem(menuFile, SWT.SEPARATOR);
		MenuItem optionFermer = new MenuItem(menuFile, SWT.PUSH);
		optionFermer.setText("Exit");
		optionFermer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		MenuItem optionOptions = new MenuItem(menu, SWT.CASCADE);
		optionOptions.setText("Options");
		optionOptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildOptionsShell();
			}
		});
		
		MenuItem optionHelp = new MenuItem(menu, SWT.CASCADE);
		Menu menuHelp = new Menu(shell, SWT.DROP_DOWN);
		optionHelp.setMenu(menuHelp);
		optionHelp.setText("Help");
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
	private static void buildOptionsShell() {
		Shell shell = new Shell();
		shell.setText("Options");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);

		Label portLabel = new Label(shell, SWT.NONE);
		portLabel.setText("Listening port: ");
		final Text port = new Text(shell, SWT.BORDER);
		port.setLayoutData(new GridData(100, 13));
		
		Label connectTimeoutLabel = new Label(shell, SWT.NONE);
		connectTimeoutLabel.setText("Connect Timeout: ");
		final Text connectTimeout = new Text(shell, SWT.BORDER);
		connectTimeout.setLayoutData(new GridData(100, 13));
		
		Label readTimeoutLabel = new Label(shell, SWT.NONE);
		readTimeoutLabel.setText("Read Timeout: ");
		final Text readTimeout = new Text(shell, SWT.BORDER);
		readTimeout.setLayoutData(new GridData(100, 13));
		
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Submit");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		
		shell.pack();
		shell.open();
	}
	
	/**
	 * Build the resources table, the main component.
	 * This resources table contains all the resources and allow the user
	 * to quickly remove, edit or add a resource.
	 */
	private static void buildResourcesTable() {
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
		resourcesTable.setHeaderVisible(true);
		resourcesTable.setLinesVisible(true);
		
		buildContextMenu();
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				IStructuredSelection select = (IStructuredSelection)e.getSelection();
				buildEditShell((Resource)select.getFirstElement());
			}
		});
		
		resourcesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.DEL) {
					removeResources();
				}
			}
		});
		
		Set<Resource> resources = ResourcesManager.getResources();
		for(Resource resource: resources) {
			addResourceItem(resource);
		}
	}
	
	/**
	 * Add an item representing a resource to the resources table.
	 * @param resource The new resource to add.
	 */
	private static void addResourceItem(Resource resource) {
		TableItem resourceItem = new TableItem(resourcesTable, SWT.NONE);
		String type = resource.getClass().equals(Database.class)? "Database" : "Bot";
		String trust = resource.getTrust()+"";
		resourceItem.setText(new String[] {type, resource.getName(), resource.getURI(), trust});
		resourceItem.setData(resource);
	}
	
	/**
	 * Remove an item representing a resource from the resources table.
	 * @param resource The resource to remove.
	 */
	private static void removeResourceItem(Resource resource) {
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
		
		MenuItem optionAdd = new MenuItem(contextMenu, SWT.PUSH);
		optionAdd.setText("Add");
		optionAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildAddShell();
			}
		});
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
		MenuItem optionRemove = new MenuItem(contextMenu, SWT.PUSH);
		optionRemove.setText("Remove");
		optionRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeResources();
			}
		});
		
		resourcesTable.setMenu(contextMenu);
	}
	
	/**
	 * Remove the resources selected in the resources table after
	 * a quick confirmation from the user.
	 * Update the resources table.
	 */
	private static void removeResources() {
		boolean confirm = MessageDialog.openConfirm(shell, "Remove resources", "Do you really want to remove those resources?");
        if(confirm) {
			TableItem[] resourceItems = resourcesTable.getSelection();
			List<Resource> resources = new ArrayList<Resource>();
			for(TableItem resourceItem: resourceItems) {
				resources.add((Resource)resourceItem.getData());
			}
			Set<Resource> notRemoved = ResourcesManager.removeResources(resources);
			for(TableItem resourceItem: resourceItems) {
				if(!notRemoved.contains(resourceItem.getData())) {
					resourceItem.dispose();
				}
			}
        }
	}
	
	/**
	 * Build the window to add a new resource.
	 * Simply a small form.
	 */
	private static void buildAddShell() {
		final Shell shell = new Shell();
		shell.setText("Add a new resource");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);

		Label typeLabel = new Label(shell, SWT.NONE);
		typeLabel.setText("Type: ");
		final Combo type = new Combo(shell, SWT.READ_ONLY);
		type.add("Database");
		type.add("Bot");
		type.setLayoutData(new GridData(100, 13));
		
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name: ");
		final Text name = new Text(shell, SWT.BORDER);
		name.setLayoutData(new GridData(100, 13));

		Label uriLabel = new Label(shell, SWT.NONE);
		uriLabel.setText("URI: ");
		final Text uri = new Text(shell, SWT.BORDER);
		uri.setLayoutData(new GridData(100, 13));

		Label trustLabel = new Label(shell, SWT.NONE);
		trustLabel.setText("Trust: ");
		final Text trust = new Text(shell, SWT.BORDER);
		trust.setLayoutData(new GridData(100, 13));
		
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Add");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Resource resource = null;
				if(type.getText().equals("Database")) {
					resource = new Database(uri.getText(), name.getText(), Integer.parseInt(trust.getText()));
				} else {
					resource = new Bot(uri.getText(), name.getText(), Integer.parseInt(trust.getText()));
				}
				if(ResourcesManager.addResource(resource)) {
					addResourceItem(resource);
				}
				shell.dispose();
			}
		});
		
		shell.pack();
		shell.open();
	}
	
	/**
	 * Build the edition window.
	 * Simply the same form than in the add window but with the information.
	 * The URI field can't be changed.
	 * @param resource The resource to be editing.
	 */
	private static void buildEditShell(final Resource resource) {
		final Shell shell = new Shell();
		shell.setText("Edit "+resource.getName());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);

		Label typeLabel = new Label(shell, SWT.NONE);
		typeLabel.setText("Type: ");
		final Combo type = new Combo(shell, SWT.READ_ONLY);
		type.add("Database");
		type.add("Bot");
		type.setText(resource.getClass().equals(Database.class)? "Database" : "Bot");
		type.setLayoutData(new GridData(100, 13));
		
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name: ");
		final Text name = new Text(shell, SWT.BORDER);
		name.setText(resource.getName());
		name.setLayoutData(new GridData(100, 13));

		Label uriLabel = new Label(shell, SWT.NONE);
		uriLabel.setText("URI: ");
		Text uri = new Text(shell, SWT.BORDER|SWT.READ_ONLY);
		uri.setText(resource.getURI());
		uri.setLayoutData(new GridData(100, 13));

		Label trustLabel = new Label(shell, SWT.NONE);
		trustLabel.setText("Trust: ");
		final Text trust = new Text(shell, SWT.BORDER);
		trust.setText(""+resource.getTrust());
		trust.setLayoutData(new GridData(100, 13));
		
		Button submit = new Button(shell, SWT.PUSH);
		submit.setText("Update");
		GridData dataSubmit = new GridData();
		dataSubmit.widthHint = 100;
		submit.setLayoutData(dataSubmit);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Resource newResource = null;
				if(type.getText().equals("Database")) {
					newResource = new Database(resource.getURI(), name.getText(), Integer.parseInt(trust.getText()));
				} else {
					newResource = new Bot(resource.getURI(), name.getText(), Integer.parseInt(trust.getText()));
				}
				if(ResourcesManager.updateResource(newResource)) {
					removeResourceItem(resource);
					addResourceItem(newResource);
				}
				shell.dispose();
			}
		});

		shell.pack();
		shell.open();
	}

	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Central Server");
		shell.setLayout(new FillLayout());
		
		buildInterface(shell);
		
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}