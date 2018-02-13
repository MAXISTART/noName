module.exports =  {
  proxyList: {
          '/api':{
              target: 'http://hz6idx.natappfree.cc/schoolStore',
              changeOrigin: true,
              pathRewrite: {
                  '^/api': ''
              }
          }
  }
};