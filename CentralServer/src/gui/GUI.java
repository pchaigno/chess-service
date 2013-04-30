package gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import core.Bot;
import core.Database;
import core.Resource;
import core.ResourcesManager;

public class GUI {
	private static Shell shell;

	public static void buildInterface(Shell shell) {
		GUI.shell = shell;
		buildMenu(shell);
		buildResourcesTable(shell);
	}
	
	private static void buildMenu(final Shell shell) {
		Menu menu = new Menu(shell, SWT.BAR);
		MenuItem optionFichier = new MenuItem(menu, SWT.CASCADE);
		optionFichier.setText("Fichier");
		Menu menuFichier = new Menu(shell, SWT.DROP_DOWN);
		final MenuItem optionStart = new MenuItem(menuFichier, SWT.PUSH);
		optionStart.setText("Start server");
		final MenuItem optionStop = new MenuItem(menuFichier, SWT.PUSH);
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
		MenuItem optionSeparator = new MenuItem(menuFichier, SWT.SEPARATOR);
		MenuItem optionFermer = new MenuItem(menuFichier, SWT.PUSH);
		optionFermer.setText("Quitter");
		optionFermer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		optionFichier.setMenu(menuFichier);
		
		MenuItem optionOptions = new MenuItem(menu, SWT.CASCADE);
		optionOptions.setText("Options");
		optionOptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildOptionsShell();
			}
		});
		
		MenuItem optionAide = new MenuItem(menu, SWT.CASCADE);
		optionAide.setText("A propos");
		optionAide.addSelectionListener(new SelectionAdapter() {
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
	
	private static void buildOptionsShell() {
		// - change the listened port;
		// - change the timeouts;
		Shell shell = new Shell();
		shell.setText("Options");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		
		// TODO Add options.
		
		shell.pack();
		shell.open();
	}
	
	private static void buildResourcesTable(Shell shell) {
		Table resourcesTable = new Table(shell, SWT.BORDER);
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
		
		Menu contextMenu = new Menu(resourcesTable);
		buildContextMenu(contextMenu, resourcesTable);
		resourcesTable.setMenu(contextMenu);
		
		Set<Resource> resources = ResourcesManager.getResources();
		for(Resource resource: resources) {
			TableItem resourceItem = new TableItem(resourcesTable, SWT.NONE);
			String type = resource.getClass().equals(Database.class)? "Database" : "Bot";
			String trust = resource.getTrust()+"";
			resourceItem.setText(new String[] {type, resource.getName(), resource.getURI(), trust});
			resourceItem.setData(resource);
		}
	}
	
	private static void buildContextMenu(Menu contextMenu, final Table resourcesTable) {
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
				}
			}
		});
		MenuItem optionRemove = new MenuItem(contextMenu, SWT.PUSH);
		optionRemove.setText("Remove");
		optionRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
                messageBox.setMessage("Do you really want to remove those resources?");
                messageBox.setText("Remove resources");
                if(SWT.YES==messageBox.open()) {
					TableItem[] resourceItems = resourcesTable.getSelection();
					Set<Resource> resources = new HashSet<Resource>();
					for(TableItem resourceItem: resourceItems) {
						resources.add((Resource)resourceItem.getData());
					}
					ResourcesManager.removeResources(resources);
                }
			}
		});
	}
	
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
				ResourcesManager.addResource(resource);
				shell.dispose();
			}
		});
		
		shell.pack();
		shell.open();
	}
	
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
				ResourcesManager.updateResource(newResource);
				shell.dispose();
			}
		});

		shell.pack();
		shell.open();
	}

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