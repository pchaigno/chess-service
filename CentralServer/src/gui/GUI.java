package gui;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import core.Database;
import core.Resource;
import core.ResourcesManager;

public class GUI {

	public static void buildInterface(Shell shell) {
		buildMenu(shell);
		buildResourcesTable(shell);
	}
	
	private static void buildMenu(final Shell shell) {
		// - start/stop server;
		// - change the listened port;
		// - change the timeouts;
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
				// TODO Show options.
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
	
	private static void buildResourcesTable(Shell shell) {
		// - add resource;
		// - remove resources;
		// - edit resource;
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
		}
	}
	
	private static void buildContextMenu(Menu contextMenu, final Table resourcesTable) {
		MenuItem optionRemove = new MenuItem(contextMenu, SWT.PUSH);
		optionRemove.setText("Remove");
		optionRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Remove selected.
			}
		});
		MenuItem optionEdit = new MenuItem(contextMenu, SWT.PUSH);
		optionEdit.setText("Edit");
		optionEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Edit selected.
			}
		});
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Central Server");
		shell.setLayout(new FillLayout());
		
		buildInterface(shell);
		
		shell.pack();
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}