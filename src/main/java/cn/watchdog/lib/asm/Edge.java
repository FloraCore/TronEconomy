package cn.watchdog.lib.asm;

/**
 * An edge in the control flow graph of a method. Each node of this graph is a basic block,
 * represented with the Label corresponding to its first instruction. Each edge goes from one node
 * to another, i.e. from one basic block to another (called the predecessor and successor blocks,
 * respectively). An edge corresponds either to a jump or ret instruction or to an exception
 * handler.
 *
 * @author Eric Bruneton
 * @see Label
 */
final class Edge {

	/**
	 * A control flow graph edge corresponding to a jump or ret instruction. Only used with {@link
	 * ClassWriter#COMPUTE_FRAMES}.
	 */
	static final int JUMP = 0;

	/**
	 * A control flow graph edge corresponding to an exception handler. Only used with {@link
	 * ClassWriter#COMPUTE_MAXS}.
	 */
	static final int EXCEPTION = 0x7FFFFFFF;

	/**
	 * Information about this control flow graph edge.
	 *
	 * <ul>
	 *   <li>If {@link ClassWriter#COMPUTE_MAXS} is used, this field contains either a stack size
	 *       delta (for an edge corresponding to a jump instruction), or the value EXCEPTION (for an
	 *       edge corresponding to an exception handler). The stack size delta is the stack size just
	 *       after the jump instruction, minus the stack size at the beginning of the predecessor
	 *       basic block, i.e. the one containing the jump instruction.
	 *   <li>If {@link ClassWriter#COMPUTE_FRAMES} is used, this field contains either the value JUMP
	 *       (for an edge corresponding to a jump instruction), or the index, in the {@link
	 *       ClassWriter} type table, of the exception type that is handled (for an edge corresponding
	 *       to an exception handler).
	 * </ul>
	 */
	final int info;

	/**
	 * The successor block of this control flow graph edge.
	 */
	final Label successor;

	/**
	 * The next edge in the list of outgoing edges of a basic block. See {@link Label#outgoingEdges}.
	 */
	Edge nextEdge;

	/**
	 * Constructs a new Edge.
	 *
	 * @param info      see {@link #info}.
	 * @param successor see {@link #successor}.
	 * @param nextEdge  see {@link #nextEdge}.
	 */
	Edge(final int info, final Label successor, final Edge nextEdge) {
		this.info = info;
		this.successor = successor;
		this.nextEdge = nextEdge;
	}
}
