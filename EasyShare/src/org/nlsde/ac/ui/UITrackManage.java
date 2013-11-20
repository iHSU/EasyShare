package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.manage.SecurityDataManage;
import security.container.model.FacetTrack;
import security.container.model.SecurityData;
import security.container.util.DateUtil;

public class UITrackManage extends JPanel implements ActionListener,
		ListSelectionListener, Observer{
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(UITrackManage.class); 

	private UITabPane parent;
	private SecurityData selectData;
	private SecurityDataManage dataManage;
	private JList<String> dataList;
	private JTable trackTable;
	private List<FacetTrack> allTracks;
	
	public UITrackManage(UITabPane _parent) {
		this.parent = _parent;
		this.dataManage = this.parent.getMainWindow().getSimpleContainer().getSecurityDataManage();
		this.parent.getMainWindow().getSimpleContainer().getSecurityDataManage().getWatcher().addObserver(this);
		this.allTracks = new ArrayList<FacetTrack>();
		initialzieUI();
		parent.add(this, BorderLayout.CENTER);
	}

	private void initialzieUI() {
		this.setLayout(new BorderLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		dataList = new JList<String>();
		dataList.setListData(dataManage.getAllSecurityVectorDatas());
		dataList.addListSelectionListener(this);
		dataList.addMouseListener(new ListClickListener());
		dataList.addMouseMotionListener(new LocalDataTipsListener());
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane leftJSP = new JScrollPane(dataList);
		leftJSP.setPreferredSize(new Dimension(250, 400));
		leftJSP.setBorder(BorderFactory.createTitledBorder("数据"));
		leftPanel.add(leftJSP, BorderLayout.CENTER);
		
		this.add(leftPanel, BorderLayout.WEST);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		Object[][] tableContent = {};
		Object[] columnTitle = {"时间", "类型", "版本", "来源", "内容"};
		trackTable = new JTable(tableContent, columnTitle);
		resizeColumnLength(trackTable);
		trackTable.addMouseMotionListener(new TableTipsListener());
		JScrollPane tableJSP = new JScrollPane(trackTable);
		tableJSP.setBorder(BorderFactory.createTitledBorder("数据访问信息"));
		centerPanel.add(tableJSP, BorderLayout.CENTER);
		this.add(centerPanel, BorderLayout.CENTER);
	}

	private void resizeColumnLength(JTable trackTable2) {
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(125);
		trackTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		trackTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		trackTable.getColumnModel().getColumn(3).setPreferredWidth(115);
		trackTable.getColumnModel().getColumn(4).setPreferredWidth(155);	
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		dataList.setListData(dataManage.getAllSecurityVectorDatas());
		dataList.updateUI();
		logger.debug("track data list update");
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) lse.getSource();
		if (list == dataList) {
			allTracks.clear();
			
			String name = list.getSelectedValue();
			selectData = dataManage.getSecurityData(name);
			if (selectData != null) {
				allTracks.addAll(selectData.getAccessTracks());
				List<FacetTrack> rwTracks = selectData.getReadWriteTracks();
				
				if (!rwTracks.isEmpty()) {
					allTracks.addAll(rwTracks);
				}
				Collections.sort(allTracks);
				trackTable.setModel(new TrackTableModel(allTracks));
				resizeColumnLength(trackTable);
				trackTable.updateUI();
			}
			else {
				logger.debug("security data not found.");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	class ListClickListener extends MouseAdapter {
		private JList<String> dataList;
		// the return value of e.getButton() is 1，2，3
		// 1: mouse left key; 3: mouse right key
		public void mouseClicked(MouseEvent e) {
			dataList = UITrackManage.this.dataList;
			int index = dataList.locationToIndex(e.getPoint());
			dataList.setSelectedIndex(index);
			// mouse right key.
			if (e.getButton() == 3
					&& dataList.getSelectedValuesList().size() >= 0) {
				return;
			}
			// mouse left double key.
			if (dataList.getSelectedIndex() != -1) {
				if (e.getClickCount() == 2) {
					// TODO
				}
			}
		}
	}
	
	class TableTipsListener extends MouseAdapter {
		public void mouseMoved(MouseEvent me) {
			int row = trackTable.rowAtPoint(me.getPoint());
			int column = trackTable.columnAtPoint(me.getPoint());
			if (row > -1 && column > -1) {
				Object value = trackTable.getValueAt(row, column);
				if ( null != value && !"".equals(value)) {
					trackTable.setToolTipText(value.toString());
				}
				else {
					trackTable.setToolTipText(null);
				}
			}
		}
	}
	
	class LocalDataTipsListener extends MouseAdapter {
		public void mouseMoved(MouseEvent me) {
			// show the tips including the owner
			int index = dataList.locationToIndex(me.getPoint());
			if (index > -1) {
				Object value = dataList.getModel().getElementAt(index);
				if ( null != value && !"".equals(value)) {
					String selectName = value.toString();
					SecurityData sd = dataManage.getSecurityData(selectName);
					dataList.setToolTipText("<html>" 
								+ "文件名: " + selectName 
								+ "<p>拥有者: " + sd.getBasic().getOwner()
								+ "<p>创建时间: " + sd.getBasic().getCreateTime()
								+ "<p>更新时间: " + sd.getBasic().getLastUpdateTime()
								+ "</html>");
				}
				else {
					dataList.setToolTipText(null);
				}
			}
		}
		
	}
	
	private class TrackTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private List<FacetTrack> tracks;
		private String[] columnNames = {"时间", "类型", "版本", "来源", "内容"};
			
		public TrackTableModel(List<FacetTrack> _tracks) {
			this.tracks = _tracks;
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return tracks.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			FacetTrack track = tracks.get(row);
			switch(col) {
			case 0:
				return DateUtil.getFormatTime(track.getRecordTime());
			case 1:
				return track.getType();
			case 2:
				return track.getVersion();
			case 3:
				return track.getOperator();
			case 4:
				return track.getLog();
			default :		
				return "";
			}
		}

		public String getColumnName(int column) {
		    return columnNames[column];
		}
	}
	
	
}
