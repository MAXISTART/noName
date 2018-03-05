module.exports =  {
  proxyList: {
    '/api':{
        target: 'http://localhost/schoolStore',
            changeOrigin: true,
            pathRewrite: {
            '^/api': ''
        }
    }
}
};