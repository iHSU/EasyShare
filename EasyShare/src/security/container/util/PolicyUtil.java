package security.container.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PolicyUtil {
	private static Log logger = LogFactory.getLog(PolicyUtil.class);

	private final String InternalNode = "node";
	private final String LeafNode = "leaf";
	
	Document policy = null;
	String path = null;
	List<String> attributes = null;
	private int nodeNum = 0;

	public PolicyUtil(String path) {
		this.path = path;
	}
	
	public PolicyUtil() {
	}

	public boolean validatePolicy(List<String> reasons) {
		// set state
		return true;
	}

	public Document getPolicy() {
		return this.policy;
	}

	public boolean toFile(String filePath) {
		if (!validatePolicy(new ArrayList<String>(0))) {
			return false;
		}
		Document doc = null;
		OutputStream out = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(path));
			out = new FileOutputStream(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		// get root: policy
		Element policy = doc.getDocumentElement();
		// get the root node in access tree
		NodeList sons = policy.getChildNodes();
		Element treeRoot = null;
		for (int i = 0; i < sons.getLength(); i++) {
			Node son = sons.item(i);
			if (son instanceof Element) {
				Element e = (Element) son;
				if (e.getTagName() == "node") {
					treeRoot = e;
					break;
				}
			}
		}

		try {
			nodeToFile(treeRoot, out, -1);
			out.write(intToBytes(nodeNum)); // write node num into file
			logger.info("访问控制树结点" + nodeNum);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean toFile(String filePath, String content) {
		if (!validatePolicy(new ArrayList<String>(0))) {
			return false;
		}
		Document doc = null;
		OutputStream out = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String tmpPath = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".policy");
			FileUtil.writeStringToFile(content, tmpPath);
			doc = builder.parse(tmpPath);
			out = new FileOutputStream(filePath);
//			SystemUtil.deleteTempFile(tmpPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		// get root: policy
		Element policy = doc.getDocumentElement();
		// get the root node in access tree
		NodeList sons = policy.getChildNodes();
		Element treeRoot = null;
		for (int i = 0; i < sons.getLength(); i++) {
			Node son = sons.item(i);
			if (son instanceof Element) {
				Element e = (Element) son;
				if (e.getTagName() == "node") {
					treeRoot = e;
					break;
				}
			}
		}

		try {
			nodeToFile(treeRoot, out, -1);
			out.write(intToBytes(nodeNum)); // write node num into file
			logger.info("访问控制树结点" + nodeNum);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void nodeToFile(Element e, OutputStream out, int father)
			throws IOException {
		// write the father position
		out.write(intToBytes(father));
		nodeNum++;
		String type = e.getTagName();
		// System.out.print(type + "\t");
		if (type == InternalNode || type == LeafNode) {
			int len = -1; // store data length type
			byte[] attrValue = null; // store attr vlaue is the node is a leaf
			if (type == LeafNode) {
				attrValue = e.getAttributes().getNamedItem("value")
						.getNodeValue().trim().getBytes();
				len = -(attrValue.length + 4 + 4 + 4); // sum of length of 'father', 'k', 'num', 'attr'
			}
			// write len info -1 into out
			out.write(intToBytes(len));
			// write k, num into out
			NamedNodeMap attributes = e.getAttributes();
			Node kNode = attributes.getNamedItem("k");
			Node numNode = attributes.getNamedItem("num");
			int k = Integer.valueOf(kNode.getNodeValue());
			int num = Integer.valueOf(numNode.getNodeValue());
			out.write(intToBytes(k));
			out.write(intToBytes(num));
			// System.out.println("(k, num): (" + k + " , " + num + ")");
			// append attr value, if this node is leaf
			if (type == LeafNode) {
				out.write(attrValue);
				System.out.println("叶子属" + new String(attrValue));
			}
			NodeList sons = e.getChildNodes();
			int sonFather = nodeNum - 1;
			for (int i = 0; i < sons.getLength(); i++) {
				Node son = sons.item(i);
				if (son instanceof Element) {
					nodeToFile((Element) son, out, sonFather);
				}
			}
		} else { // type == ExLeafNode
			NamedNodeMap attributes = e.getAttributes();
			String value = attributes.getNamedItem("value").getNodeValue()
					.trim();
			byte[] valueBytes = value.getBytes();
			int len = valueBytes.length;
			// write len info into out
			out.write(intToBytes(len));
			// write valueBytes to out
			out.write(valueBytes);
			// System.out.println("扩展属" + new String(valueBytes));
		}
	}

	private byte[] intToBytes(int value) {
		int v = value > 0 ? value : -value;
		boolean flag = value < 0 ? true : false;
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			int t = v % 256;
			bytes[i] = (byte) t;
			v /= 256;
		}
		if (flag) {
			bytes[3] = (byte) (bytes[3] | 0x80);
		}
		return bytes;
	}
}
