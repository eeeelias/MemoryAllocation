//import memory.FirstFit.Link;
//
//int addressToRelease = p.pointsAt();
//		int nextPointer;
//		int sizeToRelease = 0;
//		int address = 0;
//		int newFreeAdress = 0;
//		int totalMergeSize = 0;
//		Pointer newPointer = null;
//		int loopaFreeList = freeList.size();
//		int count = 0;
//		// Går igenom usedList
//		for (int i = 0; i < usedList.size(); i++) {
//			if (usedList.get(i).pointer.pointsAt() == addressToRelease) {
//				sizeToRelease = usedList.get(i).size;
//				nextPointer = usedList.get(i).pointer.pointsAt() + sizeToRelease + 1;
//
//				// Går igenom freeList
//				for (int j = 0; j < loopaFreeList; j++) {
//					int freeListAdress = freeList.get(j).pointer.pointsAt(); 
//					int freeListSize = freeList.get(j).size; 
//					int freeListBehind = freeListAdress + freeListSize; 
//
//					// Kollar om linken innan (-1) är ett ledigt utrymme
//					if (freeListBehind == addressToRelease - 1) {
//
//						totalMergeSize = freeListSize + usedList.get(i).size;
//						newPointer = new Pointer(freeListAdress, this);
//						newFreeAdress = freeList.get(j).pointer.pointsAt();
//						usedList.remove(i);
//						freeList.remove(j);
//						freeList.add(new Link(totalMergeSize, newPointer));
//						System.out.println("Mergat med utrymme som ligger innan, ny total: " + totalMergeSize);
//
//						// Kollar om linken efter (+1) är ett ledigt utrymme
//					} else if (freeList.get(j).pointer.pointsAt() == newFreeAdress) {
//						int mergeSize = totalMergeSize + freeListSize;
//						freeList.remove(j);
//						usedList.remove(i);
//						freeList.add(new Link(sizeToRelease, newPointer));
//						System.out.println("Mergat med utrymme som ligger efter, ny total: " + totalMergeSize);
//					} else {
//						newPointer = new Pointer(addressToRelease, this);
//						freeList.add(new Link(sizeToRelease, newPointer));
//						usedList.remove(i);
//						System.out.println("Utan merge, ny total: " + sizeToRelease);
//					}
//
//					if (freeList.get(j).pointer.pointsAt() == newFreeAdress) {
//						int mergeSize = totalMergeSize + freeListSize;
//						freeList.remove(j);
//						usedList.remove(i);
//						freeList.add(j, new Link(mergeSize, newPointer));
//						System.out.println("NYTT FRITT UTRYMME 2: " + totalMergeSize);
//					}
//
//					freeList.sort(new Link());
//				}
//
//			}
//			if (usedList.get(i).pointer.pointsAt() == addressToRelease) {
//				// if (usedList.get(i).pointer.pointsAt() == ) {
//				//
//				// }
//				usedList.remove(i);
//			}
//
//		}