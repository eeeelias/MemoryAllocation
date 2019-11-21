package memory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * This memory model allocates memory cells based on the best-fit method.
 * @author Alexander J. Drottsgård & Elias Moltedo
 */
public class BestFit extends Memory {
	private int size;
	private int memorySize;
	private LinkedList<Link> freeList = new LinkedList<Link>();
	private HashMap<Pointer, Integer> usedMap = new HashMap<Pointer, Integer>();

	/**
	 * Initializes an instance of a bestfit-based memory. 
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
		super(size);
		this.memorySize = size;
		freeList.add(new Link(size, new Pointer(0, this)));
		// TODO Implement this!
	}

	/**
	 * Inner class which describes segment of memory, contains allocated size and its address.
	 * @author drottsgard
	 */
	class Link implements Comparator {
		int size; 
		Pointer pointer; 
		
		/**
		 * Constructor for creating a segment of memory.
		 * @param size - the size of the segment
		 * @param pointer - contains the address of the segment
		 */
		Link(int size, Pointer pointer) {
			this.size = size;
			this.pointer = pointer;
		}

		public Link() {

		}

		/**
		 * Sorts the links in ascending order.
		 */
		@Override
		public int compare(Object o1, Object o2) {
			Link l1 = (Link) o1;
			Link l2 = (Link) o2;

			return l1.pointer.pointsAt() - l2.pointer.pointsAt();
		}
	}

	/**
	 * Allocates a number of memory cells using the BestFit-method.
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		int smallestPossibleFit = Integer.MAX_VALUE;
		Link link = null;
		
		for (Link linkFree : freeList) {
			if (linkFree.size == size) {
				link = linkFree;
				freeList.remove(linkFree);
				break;
			} else if (linkFree.size < smallestPossibleFit && linkFree.size >= size) {
				link = linkFree;
				smallestPossibleFit = linkFree.size;
			}

		}
		Pointer addressOfFree = null;

		if (link != null) {
			addressOfFree = new Pointer(link.pointer.pointsAt(), this);
			usedMap.put(addressOfFree, size);
			link.pointer.pointAt(link.pointer.pointsAt() + size);
			link.size -= size;
		} else {
			System.out.println("Minnet för litet FeelsBadMan  (╯°□°）╯︵ ┻━┻)");
		}
		return addressOfFree;
	}

	/**
	 * Releases a number of data cells 
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		int addressToRelease = p.pointsAt();
		int sizeToRelease = usedMap.get(p);
		int current = addressToRelease + sizeToRelease;

		Link previous = null;
		Link next = null;

		// If the free list is empty a new link is created.
		if (freeList.isEmpty()) {
			Link newLink = new Link(sizeToRelease, new Pointer(addressToRelease, this));
			freeList.add(newLink);
		} else {
			
			// Iterates through the freeList to find the free fragments closest to the one who will be released.
			for (Link freeLink : freeList) {
				previous = next;
				next = freeLink;
				if (freeLink.pointer.pointsAt() > addressToRelease) {
					next = freeLink;
					break;
				}

			}
			
			// The free link is "behind" the memory we want to release. 
			if (addressToRelease >= next.pointer.pointsAt() + next.size) {
				previous = next;
				next = null;
			}
			
			Link newLink = null;
			// The previous link is neighbor to the one we are releasing, which means we extend the previous link.
			if (previous != null && previous.pointer.pointsAt() + previous.size == addressToRelease) {
				previous.size += sizeToRelease;
				current = previous.size + previous.pointer.pointsAt();
				usedMap.remove(p);
			} else {
				newLink = new Link(sizeToRelease, new Pointer(addressToRelease, this));
				freeList.add(freeList.indexOf(next), newLink);
				usedMap.remove(p);
				current = sizeToRelease + addressToRelease;
			}
			
			// The next link (in front off) is neighbor to the one we are releasing so we extend 
			if (next != null && next.pointer.pointsAt() == current) {
				if (newLink != null) {
					newLink.size += next.size;
					freeList.remove(next);
				} else {
					// Both previous and next were neighbors, previous extends further.
					previous.size += next.size;
					freeList.remove(next);
				}

			}
		}

	}

	/**
	 * Prints a simple model of the memory. Example:
	 * 
	 * | 0 - 110 | Allocated | 111 - 150 | Free | 151 - 999 | Allocated | 1000 -
	 * 1024 | Free
	 */
	@Override
	public void printLayout() {
		LinkedList<Link> list = new LinkedList<Link>();
		Link link = null;

		// Puts the links in a LinkedList from the HashMap.
		for (Pointer pointer : usedMap.keySet()) {
			link = new Link(usedMap.get(pointer), pointer);
			list.add(link);

		}
		list.sort(link);
		System.out.println();

		if (freeList.getFirst().pointer.pointsAt() > 0) {
			System.out.println(" | 0 - " + (freeList.getFirst().pointer.pointsAt() - 1) + " | Allocated");
		}

		int dif = 0;
		int index = 0;
		for (Link free : freeList) {
			System.out.println(" | " + free.pointer.pointsAt() + " - " + (free.pointer.pointsAt() + free.size - 1)
					+ " | Free mem");

			if (index < freeList.size() - 1) {
				dif = freeList.get(index + 1).pointer.pointsAt() - (free.pointer.pointsAt() + free.size);
				if (dif > 0) {
					System.out.println(" | " + (free.pointer.pointsAt() + free.size) + " - "
							+ (freeList.get(index + 1).pointer.pointsAt() - 1) + " | Allocated");
				}
			}
			index++;
		}

		int lastLink = freeList.getLast().pointer.pointsAt() + freeList.getLast().size;
		if (lastLink < memorySize - 1) {
			System.out.println(" | " + lastLink + " - " + (memorySize - 1) + " | Allocated");
		}
		System.out.println("That's it for the memory right now");
	}
}
