const { createProxyMiddleware } = require('http-proxy-middleware');

// Configure proxy to REST API for development server
module.exports = function(app) {
  app.use(
    '/',
    function(req, res, next) {
      if (req.path === '/')
        res.redirect('/qrbill');
      else
        next();
    }
  );
  app.use(
    '/qrbill-api',
    createProxyMiddleware({
      target: 'http://localhost:8081',
      changeOrigin: true,
    })
  );
};
