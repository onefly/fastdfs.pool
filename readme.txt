本项目包含两种连接池实现方式，
pool包下引用了common.pool包下的连接池，
server包下是自己实现的连接池，
使用上述方法中的任意一种即可，使用方法参见fastdfs.xml配置文件,
然后代码中通过spring注入即可使用