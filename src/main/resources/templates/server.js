var Hapi = require('hapi');
var Good = require('good');

var server = new Hapi.Server();

server.connection({ port: process.env.PORT || 3000 });

server.views({
  engines: { jade: require('jade') },
  path: __dirname + '/app/views',
  compileOptions: {
    pretty: true,
    layoutPath: './app/views/layouts/',
    layout: false
  }
});

server.route({
    method: 'GET',
    path: '/',
    handler: function(request, reply) {
      reply.view('index');
    }
});

server.route({
    method: 'get',
    path: '/about',
    handler: function(request, reply) {
      reply.view('about/index');
    }
});

server.route({
    method: 'GET',
    path: '/fonts/{params*}',
    handler: {
        directory: {
            path: 'build/fonts/'
        }
    }
});

server.route({
    method: 'GET',
    path: '/images/{params*}',
    handler: {
        directory: {
            path: 'build/images/'
        }
    }
});

server.route({
    method: 'GET',
    path: '/application.js',
    handler: function (request, reply) {
        reply.file('build/application.js');
    }
});

server.route({
    method: 'GET',
    path: '/components.css',
    handler: function (request, reply) {
        reply.file('buildDrF/components.css');
    }
});

server.route({
    method: 'GET',
    path: '/pui-css-typography/{params*}',
    handler: {
        directory: {
            path: 'buildDrF/pui-css-typography/'
        }
    }
});

server.route({
    method: 'GET',
    path: '/application.css',
    handler: function (request, reply) {
        reply.file('build/application.css');
    }
});

server.register({
  register: Good,
  options: {
    reporters: [{
      reporter: require('good-console'),
      args:[{ log: '*', response: '*' }]
    }]
  }
  }, function(err){
    if (err) {
      throw err;
    }

  server.start(function () {
      console.log('Server running at:', server.info.uri);
  });

});
