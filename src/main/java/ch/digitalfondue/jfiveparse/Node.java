/**
 * Copyright (C) 2015 digitalfondue (info@digitalfondue.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.digitalfondue.jfiveparse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for all the nodes.
 */
public abstract class Node {

    private static final List<Node> EMPTY_LIST = Collections.emptyList();

    Node parentNode;

    public static final String NAMESPACE_HTML = "http://www.w3.org/1999/xhtml";
    public static final String NAMESPACE_SVG = "http://www.w3.org/2000/svg";
    public static final String NAMESPACE_MATHML = "http://www.w3.org/1998/Math/MathML";

    public static final String NAMESPACE_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String NAMESPACE_XML = "http://www.w3.org/XML/1998/namespace";
    public static final String NAMESPACE_XLINK = "http://www.w3.org/1999/xlink";

    /**
     * {@link Element} node type value:
     */
    public static final byte ELEMENT_NODE = 1;

    /**
     * {@link Text} node type value:
     */
    public static final byte TEXT_NODE = 3;

    /**
     * {@link Comment} node type value:
     */
    public static final byte COMMENT_NODE = 8;

    /**
     * {@link Document} node type value:
     */
    public static final byte DOCUMENT_NODE = 9;

    /**
     * {@link DocumentType} node type value:
     */
    public static final byte DOCUMENT_TYPE_NODE = 10;

    /**
     * @return the node type. See {@link #ELEMENT_NODE}, {@link #TEXT_NODE},
     *         {@link #COMMENT_NODE}, {@link #DOCUMENT_NODE} and
     *         {@link #DOCUMENT_TYPE_NODE}.
     */
    public abstract byte getNodeType();

    /**
     * @return the node name. Each concrete class will return a specific value.
     */
    public abstract String getNodeName();

    /**
     * Get the parent node if present or else return null.
     */
    public Node getParentNode() {
        return parentNode;
    }

    List<Node> getMutableChildNodes() {
        return EMPTY_LIST;
    }

    /**
     * Remove all child nodes from this node.
     */
    public void empty() {
        List<Node> childs = getMutableChildNodes();
        if (childs == EMPTY_LIST) {
            return;
        }

        for (Node n : childs) {
            n.parentNode = null;
        }

        childs.clear();
    }

    /**
     * Get the number of childs of the current node.
     */
    public int getChildCount() {
        return getMutableChildNodes().size();
    }

    /**
     * Append the {@link Node} at the end of this node. If the node has a
     * parentNode defined, it will be removed from the original parent.
     */
    public void appendChild(Node node) {
        List<Node> childs = getMutableChildNodes();
        if (childs == EMPTY_LIST) {
            return;
        }

        if (node.parentNode == this) {
            childs.remove(node);
            node.parentNode = null;
        }

        insertChildren(childs.size(), node);
    }

    /**
     * Insert the {@link Node} at the given position. If the node has a
     * parentNode defined, it will be removed from the original parent.
     * 
     * @param position
     *            the index
     * @param node
     *            the node to be inserted
     */
    public void insertChildren(int position, Node node) {
        List<Node> childs = getMutableChildNodes();
        if (childs == EMPTY_LIST) {
            return;
        }

        Node previousParent = node.parentNode;
        node.parentNode = this;

        if (position == childs.size()) {
            childs.add(node);
        } else {
            childs.add(position, node);
        }

        if (previousParent != null) {
            previousParent.getMutableChildNodes().remove(node);
        }
    }

    /**
     * Insert the {@link Node} before another {@link Node}.
     * 
     * @param toInsert
     *            the node to be inserted
     * @param before
     *            if the node is not a child of this node, the insertion will
     *            fail silently.
     */
    public void insertBefore(Node toInsert, Node before) {
        int idx = getChildNodes().indexOf(before);
        if (idx >= 0) {
            insertChildren(idx, toInsert);
        }
    }

    /**
     * Replace a node with another one.
     * 
     * @param node
     *            the new node
     * @param oldChild
     *            the node to be replaced
     */
    public void replaceChild(Node node, Node oldChild) {
        List<Node> childs = getMutableChildNodes();
        if (childs == EMPTY_LIST) {
            return;
        }
        int idx = childs.indexOf(oldChild);
        if (idx >= 0) {
            Node previousParent = node.parentNode;
            node.parentNode = this;
            childs.set(idx, node);
            if (previousParent != null) {
                previousParent.getMutableChildNodes().remove(node);
            }
            oldChild.parentNode = null;
        }
    }

    /**
     * Remove a child node.
     * 
     * @param node
     *            the node to be removed
     */
    public void removeChild(Node node) {
        List<Node> childs = getMutableChildNodes();
        if (childs == EMPTY_LIST) {
            return;
        }
        if (childs.remove(node)) {
            node.parentNode = null;
        }
    }

    /**
     * Get the child nodes. The list is <strong>not</strong> modifiable.
     */
    public List<Node> getChildNodes() {
        return EMPTY_LIST;
    }

    /**
     * Get the first child, if present or else null.
     */
    public Node getFirstChild() {
        List<Node> childs = getChildNodes();
        return childs.isEmpty() ? null : childs.get(0);
    }

    /**
     * Get the last child, if present or else null.
     */
    public Node getLastChild() {
        List<Node> childs = getChildNodes();
        return childs.isEmpty() ? null : childs.get(childs.size() - 1);
    }

    /**
     * Get the first <strong>{@link Element}</strong> child, if present or else
     * null.
     */
    public Element getFirstElementChild() {
        List<Node> childs = getChildNodes();
        for (Node n : childs) {
            if (n.getNodeType() == ELEMENT_NODE) {
                return (Element) n;
            }
        }
        return null;
    }

    /**
     * Get the last <strong>{@link Element}</strong> child, if present or else
     * null.
     */
    public Element getLastElementChild() {
        List<Node> childs = getChildNodes();
        for (int i = childs.size() - 1; i >= 0; i--) {
            Node n = childs.get(i);
            if (n.getNodeType() == ELEMENT_NODE) {
                return (Element) n;
            }
        }
        return null;
    }

    /**
     * Get the previous sibling {@link Node} if present, or else null.
     */
    public Node getPreviousSibling() {
        if (parentNode == null) {
            return null;
        }

        List<Node> siblings = parentNode.getChildNodes();
        int currentElemIdx = siblings.indexOf(this);
        return currentElemIdx == 0 ? null : siblings.get(currentElemIdx - 1);
    }

    /**
     * Get the previous <strong>{@link Element}</strong> sibling if present, or
     * else null.
     */
    public Element getPreviousElementSibling() {
        Node n = getPreviousSibling();
        while (n != null) {
            if (n.getNodeType() == ELEMENT_NODE) {
                return (Element) n;
            }
            n = n.getPreviousSibling();
        }
        return null;
    }

    /**
     * Get the next sibling {@link Node} if present, or else null.
     */
    public Node getNextSibling() {
        if (parentNode == null) {
            return null;
        }

        List<Node> siblings = parentNode.getChildNodes();
        int currentElemIdx = siblings.indexOf(this);

        return currentElemIdx == siblings.size() - 1 ? null : siblings.get(currentElemIdx + 1);
    }

    /**
     * Get the next <strong>{@link Element}</strong> sibling if present, or else
     * null.
     */
    public Element getNextElementSibling() {
        Node n = getNextSibling();
        while (n != null) {
            if (n.getNodeType() == ELEMENT_NODE) {
                return (Element) n;
            }
            n = n.getNextSibling();
        }
        return null;
    }

    /**
     * @return true if this node has at least one child.
     */
    public boolean hasChildNodes() {
        return !getChildNodes().isEmpty();
    }

    /**
     * Traverse the childs of this node in <a href=
     * "https://html.spec.whatwg.org/multipage/infrastructure.html#tree-order"
     * >"tree order"</a>.
     */
    // As described in
    // http://www.drdobbs.com/database/a-generic-iterator-for-tree-traversal/184404325
    public void traverse(NodesVisitor visitor) {
        Node node = getFirstChild();
        while (node != null) {
            visitor.start(node);
            if (visitor.complete()) {
                return;
            }
            if (node.hasChildNodes()) {
                node = node.getFirstChild();
            } else {
                while (node != this && node.getNextSibling() == null) {
                    visitor.end(node);
                    if (visitor.complete()) {
                        return;
                    }
                    node = node.getParentNode();
                }

                if (node == this) {
                    break;
                }
                visitor.end(node);
                if (visitor.complete()) {
                    return;
                }

                node = node.getNextSibling();
            }
        }
    }

    /**
     * Traverse this node and his child.
     */
    public void traverseWithCurrentNode(NodesVisitor visitor) {
        visitor.start(this);
        traverse(visitor);
        visitor.end(this);
    }

    /**
     * Get all the nodes matching the given matcher. The nodes will be returned
     * in "tree order". See {@link Selector}.
     */
    public <T extends Node> List<T> getAllNodesMatching(NodeMatcher matcher) {
        return getAllNodesMatching(matcher, false);
    }

    /**
     * Get all the nodes matching the given matcher. The nodes will be returned
     * in "tree order". If the second parameter is true, the traversal will stop
     * on the first match. See {@link Selector}.
     */
    public <T extends Node> List<T> getAllNodesMatching(NodeMatcher matcher, boolean onlyFirstMatch) {
        List<T> l = new ArrayList<>();
        traverse(new NodeMatchers<>(matcher, l, onlyFirstMatch));
        return l;
    }

    /**
     * Get all the {@link Element} that match the given name. The elements will
     * be returned in "tree order". The name is case sensitive.
     */
    public List<Element> getElementsByTagName(String name) {
        return getAllNodesMatching(new NodeMatchers.ElementHasTagName(name));
    }

    /**
     * Get all the {@link Element} that match the given name and namespace. The
     * elements will be returned in "tree order". The name and namespace are
     * case sensitive.
     */
    public List<Element> getElementsByTagNameNS(String name, String namespace) {
        return getAllNodesMatching(new NodeMatchers.ElementHasTagName(name, namespace));
    }

    /**
     * Get the element with the given id. The id is case sensitive. If in the
     * documents there are more than one element with the same id, the first
     * element found during the traversal will be returned.
     */
    public Element getElementById(String idValue) {
        List<Element> l = getAllNodesMatching(new NodeMatchers.HasAttribute("id", idValue, NodeMatchers.ATTRIBUTE_MATCH_VALUE_EQ), true);
        return l.isEmpty() ? null : l.get(0);
    }

    /**
     * Return true if node is descendant.
     * 
     * @param node
     * @return
     */
    public boolean contains(Node node) {
        return !getAllNodesMatching(new NodeMatchers.NodeIsEqualReference(node), true).isEmpty();
    }

    /**
     * Get the text content of the node.
     */
    public String getTextContent() {
        List<Text> textNodes = getAllNodesMatching(new NodeMatchers.NodeHasType(TEXT_NODE));
        StringBuilder sb = new StringBuilder();
        for (Text n : textNodes) {
            sb.append(n.getData());
        }
        return sb.toString();
    }

    /**
     * Get the html content of the child of this node.
     */
    public String getInnerHTML() {
        return getInnerHTML(EnumSet.noneOf(Option.class));
    }

    /**
     * 
     * Get the html content of the child of this node.
     *
     * @param options
     *            serialization {@link Option}.
     * @return
     */
    public String getInnerHTML(Set<Option> options) {
        StringBuilder sb = new StringBuilder();
        traverse(new HtmlSerializer(sb, options));
        return sb.toString();
    }

    /**
     * Get the html content of the this node and his child.
     */
    public String getOuterHTML() {
        return getOuterHTML(EnumSet.noneOf(Option.class));
    }

    /**
     * Get the html content of the child of this node.
     * 
     * @param options
     *            serialization {@link Option}.
     * @return
     */
    public String getOuterHTML(Set<Option> options) {
        StringBuilder sb = new StringBuilder();
        traverseWithCurrentNode(new HtmlSerializer(sb, options));
        return sb.toString();
    }

}
