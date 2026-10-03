# orm day 1 - lab 1 (hibernate native)

run com.training.Main, the console shows the sql for every step, the lifecycle part and the cache part.

**lifecycle, one line each**
- transient: just created with new, no id, hibernate doesnt know about it
- persistent: attached to an open session, hibernate tracks it and writes changes on flush/commit
- detached: was persistent but the session closed, changes arent tracked anymore
- removed: marked for delete with session.remove, row is gone once you commit

**cache**
two get() calls for the same id in one session give back the same object and only one select runs. open a new session and it runs the select again, so the first level cache only lives as long as the session.

**part F**
with plain jdbc the raise would be writing the UPDATE by hand, preparing the statement, binding the params in the right order, executing it and closing everything. here i just change the salary on a managed object and dirty checking creates the update at commit.
