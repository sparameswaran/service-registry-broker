/** @jsx React.DOM */
var React = require('react');
var Router = require('react-router');
var _ = require('lodash');
var DefaultButton = require('pui-react-buttons').DefaultButton;

var RegistryServices = require('../shared/registryServices.jsx');
var CredentialsEditor = require('./credentialsEditor.jsx');
var TagsEditor = require('./tagsEditor.jsx');

(function () {
    'use strict';

    var EditService = React.createClass({

        getInitialState: function () {
            var jsonPayload = {};
            var space = '\t';

            var serviceId = this.props.params.serviceId;

            console.log("Current props includes: ", this.props);
            console.log("Current state includes: ", this.state);

            this.setState({
                serviceId: serviceId
            });

            var serviceName = '';
            var serviceDescrp = '';
            var serviceLongDescrp = '';
            var imageUrl = '';
            var displayName = '';
            var providerDisplayName = '';
            var docUrl = '';
            var supportUrl = '';

            RegistryServices.findServiceById(serviceId)
                .done(this.reloadService);

            return {
                serviceId: serviceId
            };
        },

        reloadService: function (serviceEntry) {

            var name = serviceEntry.name;
            var description = serviceEntry.description;
            var longDescription = serviceEntry.metadata.longDescription;
            var imageUrl = serviceEntry.metadata.imageUrl;
            var displayName = serviceEntry.metadata.displayName;
            var providerDisplayName = serviceEntry.metadata.providerDisplayName;
            var documentationUrl = serviceEntry.metadata.documentationUrl;
            var supportUrl = serviceEntry.metadata.supportUrl;

            this.setState({
                service: serviceEntry,
                name: name,
                description: description,
                longDescription: longDescription,
                imageUrl: imageUrl,
                displayName: displayName,
                providerDisplayName: providerDisplayName,
                documentationUrl: documentationUrl,
                supportUrl: supportUrl
            });
        },

        handleSubmit: function (event) {

            console.log("Got handleSubmitRow! with state: ", this.state);

            var name = this.refs
                .name
                .getDOMNode()
                .value;
            var description = this.refs
                .description
                .getDOMNode()
                .value;
            var displayName = this.refs
                .displayName
                .getDOMNode()
                .value;
            var longDescription = this.refs
                .longDescription
                .getDOMNode()
                .value;
            var providerDisplayName = this.refs
                .providerDisplayName
                .getDOMNode()
                .value;
            var documentationUrl = this.refs
                .documentationUrl
                .getDOMNode()
                .value;
            var supportUrl = this.refs
                .supportUrl
                .getDOMNode()
                .value;
            var imageUrl = this.refs
                .imageUrl
                .getDOMNode()
                .value;

            var servicePayload = {
                id: this.state.serviceId,
                bindable: true,
                description: description,
                name: name,
                metadata: {
                    displayName: displayName,
                    imageUrl: imageUrl,
                    longDescription: longDescription,
                    providerDisplayName: providerDisplayName,
                    documentationUrl: documentationUrl,
                    supportUrl: supportUrl
                }
            };

            console.log("Got handleSubmit data: ", servicePayload);
            console.log("Got handleSubmit data JSON: ", JSON.stringify(servicePayload));

            var serviceId = this.state.serviceId;
            var planId = this.state.planId;

            console.log("Going to do an edit of the service with the payload and redirect to: ", serviceId);

            RegistryServices.editService(serviceId, JSON.stringify(servicePayload));

            var router = Router.create({});
            router.transitionTo('/service/' + serviceId);
        },

        render: function () {
            var space = '  ';

            console.log("Inside render: Current state includes: ", this.state);

            if (typeof(this.state.name) == "undefined" || this.state.name == '') {
                return null;
            }

            return (

                <div className="page-title bg-neutral-11 pvxl">
                    <div className="container">
                        <div className="media-body">

                            <p className='h1 type-dark-1 mvn em-low'>Service Editor</p>

                            <form id="outerForm" role="form">

                                <label for="name">
                                    <h4>Service Name</h4>
                                </label>
                                <input className="form-control" type="text" ref="name" placeholder="Edit Service Name" defaultValue={this.state.name}/>
                                <br/>
                                <label for="description">
                                    <h4>Service Description</h4>
                                </label>
                                <input className="form-control" type="text" ref="description" placeholder="Enter Service Description" defaultValue={this.state.description}/>
                                <br/>
                                <label for="description">
                                    <h4>Display name</h4>
                                </label>
                                <input className="form-control" type="text" ref="displayName" placeholder="Enter Display Name" defaultValue={this.state.displayName}/>
                                <br/>
                                <label for="description">
                                    <h4>Long Description</h4>
                                </label>
                                <input className="form-control" type="text" ref="longDescription" placeholder="Enter Long Description" defaultValue={this.state.longDescription}/>
                                <br/>
                                <label for="description">
                                    <h4>Provider Description</h4>
                                </label>
                                <input className="form-control" type="text" ref="providerDisplayName" placeholder="Enter Long Provider Description" defaultValue={this.state.providerDisplayName}/>
                                <br/>
                                <label for="description">
                                    <h4>Image Url</h4>
                                </label>
                                <input className="form-control" type="text" ref="imageUrl" placeholder="Enter Image Url" defaultValue={this.state.imageUrl}/>
                                <br/>
                                <label for="description">
                                    <h4>Documentation Url</h4>
                                </label>
                                <input className="form-control" type="text" ref="documentationUrl" placeholder="Enter Documentation Url" defaultValue={this.state.documentationUrl}/>
                                <br/>
                                <label for="description">
                                    <h4>Support Url</h4>
                                </label>
                                <input className="form-control" type="text" ref="supportUrl" placeholder="Enter Support Url" defaultValue={this.state.supportUrl}/>
                                <br/>

                                <div align="left">
                                    <DefaultButton id="submit" className="btn btn-primary" onClick={this.handleSubmit}>
                                        Submit
                                    </DefaultButton>
                                </div>

                            </form>
                            <br/><br/>

                        </div>
                    </div>
                </div>

            );

        }

    });

    module.exports = EditService;

}());
