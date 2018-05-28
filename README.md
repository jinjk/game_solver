# game_solver

```sql
declare
object_exists integer := 0;
begin
    select count(1) into object_exists from sys.all_tables where owner='SCOTT' and table_name=UPPER('book');
    if object_exists > 0 then
        DBMS_OUTPUT.PUT_LINE('table found');
    end if;
    select count(1) into object_exists from sys.all_indexes where owner='SCOTT' and index_name=UPPER('book_index');
    if object_exists > 0 then
        DBMS_OUTPUT.PUT_LINE('index found');
    end if;
end;
```
