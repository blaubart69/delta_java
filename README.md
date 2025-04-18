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
The way you get a DIFF between them is a FULL OUTER JOIN.
1. keys are equal  
  => a SAME or MODIFIED
2. key on side A is NULL  
=> ONLY_IN_B ==> NEW
3. key on side B is NULL  
you have a ONLY_IN_B ==> DELETED

Let's do it in code.

The idea is to have the two lists **SORTED** by their keys.  
Then you can gain the benefit of detecting NEW and DELETED in one go.

Sample:

| Key-A | Key-B | keys  | outcome                                                              | Attributes A | Attributes B | 
|-------|-------|-------|----------------------------------------------------------------------|--------------|---------|
| 1 | 1     | equal | same or modified, compare attributes                                 | ... | .. |
| 2 | 3     | a LESS b | key a do not exist in list b - DELETE A, iterator A is moved forward |... |.. |
| 3 | 4     |       | ...                                                                  |... |.. |
| 5 | 6     | ...   | ...                                                                  |... |.. |

By have them sorted by their keys we can do the following:

1. get an iterator for each of the lists
2. get the value for each iterator
3. let's compare the keys:  
a. 


