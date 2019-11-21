package memory;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import edu.princeton.cs.algs4.MinPQ;

/**
 * This memory model allocates memory cells based on the first-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFitExtra extends Memory {
	private LinkedList<Link> freeList = new LinkedList<Link>();
	private LinkedList<Link> usedList = new LinkedList<Link>();
	private HashMap<Pointer, Integer> usedMap = new HashMap<Pointer, Integer>();
	private int memorySize;

	class Link implements Comparator {
		int size; // storlek
		Pointer pointer; // Pointer, pekar på vilken adress processen har

		Link(int number, Pointer pointer) {
			this.size = number;
			this.pointer = pointer;
		}

		public Link() {

		}

		@Override
		public int compare(Object o1, Object o2) {
			Link l1 = (Link) o1;
			Link l2 = (Link) o2;

			return l1.pointer.pointsAt() - l2.pointer.pointsAt();
		}
	}

	/**
	 * Initializes an instance of a first fit-based memory.
	 * 
	 * @param size
	 *            The number of cells.
	 */
	public FirstFitExtra(int size) {
		super(size);
		this.memorySize = size;
		freeList.add(new Link(size, new Pointer(0, this)));
	}

	/**
	 * Allocates a number of memory cells.
	 * 
	 * @param size
	 *            the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		Pointer nextAddress = null;
		Pointer oldAddress = null;
		for (Link linkFree : freeList) {
			if (linkFree.size >= size) {
				oldAddress = new Pointer(linkFree.pointer.pointsAt(), this);
				nextAddress = new Pointer(linkFree.pointer.pointsAt() + size, this);
				// Om freeLinken blir 0, då tar vi bort den linken
				if (linkFree.size == size) {
					freeList.remove(linkFree);
				} else {
					linkFree.size -= size;
					linkFree.pointer = nextAddress;
				}
				usedMap.put(oldAddress, size);
				break;
			}
		}
		if (nextAddress == null) {
			System.out.println("Minnet för litet FeelsBadMan  (╯°□°）╯︵ ┻━┻)");
		}
		// System.out.println("ADDRESS: " + nextAddress.pointsAt());
		return oldAddress;
	}

	/**
	 * Releases a number of data cells
	 * 
	 * @param p
	 *            The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		int addressToRelease = p.pointsAt(); 
		int sizeToRelease = usedMap.get(p); 
		int total = addressToRelease + sizeToRelease; 
		int freeAddress = 0;
		int index = 0; 
		boolean removeAfter = false;

		if (freeList.isEmpty()) {
			Link newLink = new Link(sizeToRelease, new Pointer(addressToRelease, this));
			System.out.println("Skapar ny link då ingen tidigare freeLink fanns");
			System.out.println("AddressToRelease: " + addressToRelease + " sizeToRelease: " + sizeToRelease);
			freeList.add(newLink);
		} else {
			for (Link freeLink : freeList) {
				index = freeList.indexOf(freeLink);
				// System.out.println("index: " + index);
				int freeSize = freeLink.size;
				freeAddress = freeLink.pointer.pointsAt(); 
				// System.out.println("freeAddress: " + freeAddress);
				int freeTotal = freeSize + freeAddress; 

				if (freeAddress < addressToRelease) {
					if (freeTotal == addressToRelease) {
						freeLink.size += sizeToRelease;
						total = freeLink.size;
						usedMap.remove(p);
						break;
					} else if (index + 1 < freeList.size() -1) {
						if (freeList.get(index + 1).pointer.pointsAt() == total) {
							freeList.get(index).size += freeList.get(index + 1).size;
							System.out.println("mergar med en free bakom som har storlek: " + freeList.get(index).size);
							index = index + 1;
							removeAfter = true;
						}
					}
				} else {
					System.out.println("Skapar ny link som inte har några fria 'grannar'");
					System.out.println("AddressToRelease: " + addressToRelease + " sizeToRelease: " + sizeToRelease);
					Link newLink = new Link(sizeToRelease, new Pointer(addressToRelease, this));
					if (addressToRelease < freeAddress) {
						System.out.println(addressToRelease + "<" + freeAddress);
						System.out.println("Lägger till ny på index " + index + " i listan: nya linkens address = "
								+ addressToRelease + " gamla länkens address = " + freeAddress);
						freeList.add(index, newLink);
					}
					usedMap.remove(p);
					break;
				}

				// if (index + 1 < freeList.size() -1) {
//				if (freeList.get(index + 1).pointer.pointsAt() == total) {
//					freeList.get(index).size += freeList.get(index + 1).size;
//					System.out.println("mergar med en free bakom som har storlek: " + freeList.get(index).size);
//					index = index + 1;
//				}
				// }

			}
			if (removeAfter) {
				freeList.remove(index + 1);
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

		// sätter in linksen från HashMap till LinkedList
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
			// skriv ut fri i varje iteration!
			System.out.println(" | " + free.pointer.pointsAt() + " - " + (free.pointer.pointsAt() + free.size - 1)
					+ " | Free mem");

			if (index < freeList.size() - 1) {
				dif = freeList.get(index + 1).pointer.pointsAt() - (free.pointer.pointsAt() + free.size);
				if (dif > 0) {
					// skriva ut allocated
					System.out.println(" | " + (free.pointer.pointsAt() + free.size) + " - "
							+ (freeList.get(index + 1).pointer.pointsAt() - 1) + " | Allocated");
				}
			}
			index++;
		}

		int lastLink = freeList.getLast().pointer.pointsAt() + freeList.getLast().size;
		if (lastLink < memorySize - 1) {
			System.out.println(" | " + (lastLink) + " - " + (memorySize - 1) + " | Allocated");
		}

		System.out.println();
		// TODO Implement this!
	}

	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		// TODO Implement this!
	}
}
