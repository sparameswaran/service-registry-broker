// React stuff
var React = require('react');

//Routing stuff
var Router = require('react-router');
var Route = Router.Route;
var NotFoundRoute = Router.NotFoundRoute;
var DefaultRoute = Router.DefaultRoute;
var Link = Router.Link;
var RouteHandler = Router.RouteHandler;

//Partials
var Home = require('./home/home.jsx');
var Header = require('./shared/header.jsx');
var Service = require('./service/service.jsx');
var EditService = require('./edit/editService.jsx');
var AddServices = require('./edit/addServices.jsx');
var EditPlan = require('./edit/editPlan.jsx');
var TestPlan = require('./edit/testPlan.jsx');
var RegistryServices = require('./shared/registryServices.jsx');

var App = React.createClass({

    getInitialState: function () {
        return {
            serviceObjects: []
        };
    },

    componentDidMount: function () {
        RegistryServices.findAllServices()
            .done(services => this.setState({
                allServiceObjects: services
            }));

    },
    render: function () {
        topLevelStateSetter = this.setState;
        return (
            <div>
                <Header></Header>
                <RouteHandler services={this.state.allServiceObjects}/>
            </div>
        );
    }
});

var routes = (
    <Route name="app" path="/" handler={App}>
        <DefaultRoute handler={Home}/>
        <Route name="/addServices" handler={AddServices}/>
        <Route name="/editService/:serviceId" handler={EditService}/>
        <Route name="/addPlan/:serviceId" handler={EditPlan}/>
        <Route name="/editPlan/:planId" handler={EditPlan}/>
        <Route name="/testPlan" handler={TestPlan}/>
        <Route name="/service/:serviceId" handler={Service}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});

module.exports = Router;