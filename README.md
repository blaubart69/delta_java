# Story

Often I found myself doing some kind of sync.  
A delta between two list.  
The way I often saw this problem implemented in the past: 

1. load the two list in memory
2. iterate list A and search the matching key in list B.
```
if   FOUND in B => SAME or MODIFIED  
else            => ONLY_IN_A aka DELETED 
```
3. iterate list B
```
if   NOT FOUND in A => ONLY_IN_B aka NEW
```
What I don't like:
- having the lists **IN MEMORY** in order to access it via hash
- traversing/searching in the lists **TWO TIMES**

I thought there must be a better way of doing this.

# Idea

If you think about the lists as two tables within a SQL DB.  

Table/List A and B with only their keys:

```
Keys in A: 1,2,3,5
Keys in B: 1,4,6
```

The database way you get a DIFF between them is a FULL OUTER JOIN.

| Key A | Key B |
|-------|-------|
| 1     | 1     |
| 2     | NULL  |
| 3     | NULL  |
| NULL  | 4     |
| 5     | NULL  |
| 6     | 6     |

So when you comparing stuff, calculating the delta between before (A) and after (B).  
The NULLs give you the information:  
(DON'T) EXISTS on this side.

+ NEW - only exists on side B
+ DELETED - only exists on side A

If the key exists on both side you get the cases:
+ SAME - key and attributes of both A and B are the same
+ MODIFIED - attributes are different

# Code

The idea is to have the two lists **SORTED** by their keys.  
Then you can gain the benefit of detecting NEW and DELETED **in one go**.

```
1. get an iterator for each of the lists
2. enhance each iterator to the next item.
3. IF iterA.elem == elemB.iter
   enhance BOTH iterators
   ==> SAME or EQUAL
4. IF iterA.elem < elemB.iter
   elem-A is smaller. no elem-B on the other side. B is NULL.
   enhance ONLY iterator A!
   ==> elem A was DELETED
5. IF iterA.elem > elemB.iter
   elem-B is smaller. no elem-A on the other side. A is NULL.
   enhance ONLY iterator B!
   ==> elem B is NEW
```

link to [code](https://github.com/blaubart69/delta_java/blob/9d5b2350c59081b59f96d83e8ffeec1572021aa2/src/main/java/at/spindi/Delta.java#L34)

 


