module.exports =  {
  proxyList: {
          '/api':{
              target: 'http://y58rni.natappfree.cc',
              changeOrigin: true,
              pathRewrite: {
                  '^/api': ''
              }
          }
  }
};