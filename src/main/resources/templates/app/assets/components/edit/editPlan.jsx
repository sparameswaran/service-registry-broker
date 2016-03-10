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

    var EditPlan = React.createClass({

        getInitialState: function () {
            var jsonPayload = {};
            var space = '\t';

            var planId = this.props.params.planId;

            console.log("EditPlan, Current props includes: ", this.props);
            console.log("EditPlan, Current state includes: ", this.state);

            var planName = '';
            var planDescrp = '';
            var cost = 0.0;
            var visible = false;
            var free = true;
            var units = 'MONTHLY';
            var currency = 'usd';
            var rawTagList = [];
            var tagList = [];
            var serviceId;

            // If we are here for editing of a plan
            // then use the available data...
            if ((typeof(planId) != "undefined") && (typeof(this.props.services) != "undefined")) {

                this.setState({
                    planId: this.props.params['planId']
                });

                var serviceEntry = _.filter(this.props.services, {
                    plans: [
                        {
                            id: planId
                        }
                    ]
                })[0];
                serviceId = serviceEntry.id;
                this.setState({
                    serviceId: serviceId
                });

                console.log("Found matching service Entry: ", serviceEntry.plans);
                var planEntry = _.filter(serviceEntry.plans, {
                    id: planId
                })[0];
                console.log("Found matching plan Entry: ", planEntry);

                // The Plan might have changed compared to whatever is saved in the
                //this.props.services.
                // So reload the state...

                RegistryServices.findPlanById(planId)
                    .done(this.reloadPlan);

            } else {

                // This means this is for a brand new plan..
                // Grab the service id so we can use that for submission later..
                serviceId = this.props.params['serviceId'];
                this.setState({
                    serviceId: serviceId
                });
            }

            return {
                planId: planId,
                serviceId: serviceId,
                planName: planName,
                planDescrp: planDescrp,
                free: free,
                visible: visible,
                cost: cost,
                units: units,
                currency: currency,
                tagList: tagList
            };
        },

        reloadPlan: function (planEntry) {

            var planId = planEntry.id;
            var planName = planEntry.name;
            var planDescrp = planEntry.description;

			var free = planEntry.free;
			var visible = planEntry.visible;
            var cost = 0.0;
            var units = 'MONTHLY';
            var currency = 'usd';
            var rawTagList = [];
            var tagList = [];

            if (typeof(planEntry.metadata.costs[0]) != "undefined") {
                console.info("Costs is ", planEntry.metadata.costs);
                console.info("Costs[0] is ", planEntry.metadata.costs[0]);
                for (var key in planEntry.metadata.costs[0].amount) {
                    currency = key;
                }

                units = planEntry.metadata.costs[0].unit;
                cost = parseFloat(planEntry.metadata.costs[0].amount[currency]);
            }
            rawTagList = planEntry.metadata.bullets;

            console.log("Edit Plan, currency: ", currency, " and cost is: ", cost);

            if (typeof(rawTagList) != "undefined") {
                tagList = [];
                for (var i = 0; i < rawTagList.length; i++) {
                    tagList.push({
                        cvalue: rawTagList[i]
                    });
                }
            }
            console.log("Updated tag list: ", tagList);

            /*
			 this.setState({ plan : planEntry });
				this.setState({ planId: planEntry.id}); 
			    this.setState({ planName: planName }); 
			    this.setState({ planDescrp: planDescrp }); 
			    this.setState({ cost: cost }); 
			    this.setState({ units: units }); 
			    this.setState({ currency: currency });
			    this.setState({ tagList : tagList});
			 */

            this.setState({
                plan: planEntry,
                planId: planEntry.id,
                planName: planName,
                planDescrp: planDescrp,
                free: free,
                visible: visible,
                cost: cost,
                units: units,
                currency: currency,
                tagList: tagList
            });
        },

		handleChange: function(field, e) {
		    var nextState = {}
		    nextState[field] = e.target.checked
		    this.setState(nextState)
		  },
		  
        handleSubmit: function (event) {

            console.log("Got handleSubmitRow! with state: ", this.state);

            // Get values via this.refs
            var planPayload = {
                name: this.refs
                    .name
                    .getDOMNode()
                    .value,
                description: this.refs
                    .description
                    .getDOMNode()
                    .value,
                free: this.refs
                    .freePlan
                    .getDOMNode()
                    .checked  
            }

            var cost = parseInt(this.refs.cost.getDOMNode().value);
            var units = this.refs
                .units
                .getDOMNode()
                .value;
            var currencyStr = this.refs
                .currency
                .getDOMNode()
                .value;

            var amount = {};
            amount[currencyStr] = cost;
            amount.toString = function () {
                return {
                    currencyStr: cost
                };
            }

            planPayload['metadata'] = {
                costs: [
                    {
                        amount,
                        unit: units

                    }
                ]
            };

            planPayload['credentials'] = this.refs
                .credentials
                .toString();
            planPayload['metadata']['bullets'] = this.refs
                .tags
                .toString();
            console.log("Got handleSubmit data: ", planPayload);
            console.log("Got handleSubmit data JSON: ", JSON.stringify(planPayload));

            var serviceId = this.state.serviceId;
            var planId = this.state.planId;

            if (typeof(planId) == "undefined") {

                console.log("Going to do an add of plan to service of the payload and redirect to: ", serviceId);

                RegistryServices.addPlanToService(serviceId, JSON.stringify(planPayload));

                var router = Router.create({});
                router.transitionTo('/service/' + serviceId);

            } else {

                console.log("Going to do an edit of the plan with the payload and redirect to: ", serviceId);

                RegistryServices.editPlan(planId, JSON.stringify(planPayload));

                var router = Router.create({});
                router.transitionTo('/service/' + serviceId);

            }
        },

        render: function () {
            var space = '  ';

            console.log("Inside render: Current state includes: ", this.state);

            if ((this.state.planId != undefined) && (typeof(this.state.planName) == "undefined" || this.state.planName == '')) {
                return null;
            }

            console.log('Plan name is already filled or planId is undefined...');

            return (

                <div className="page-title bg-neutral-11 pvxl">
                    <div className="container">
                        <div className="media-body">

                            <p className='h1 type-dark-1 mvn em-low'>Plan Editor</p>

                            <form id="outerForm" role="form">

                                <label for="name">
                                    <h4>Plan Name</h4>
                                </label>
                                <input className="form-control" type="text" ref="name" placeholder="Enter Plan Name" defaultValue={this.state.planName}/>
                                <br/>
                                <label for="description">
                                    <h4>Plan Description</h4>
                                </label>
                                <input className="form-control" type="text" ref="description" placeholder="Enter Plan Description" defaultValue={this.state.planDescrp}/>
                                <br/>
                                <div>
                                    <label for="freePlan" class="mrs">
                                        <h4>Free Plan &nbsp;
                                        </h4>
                                    </label>
                                    <input type="checkbox" ref="freePlan" checked={this.state.free} onChange={this.handleChange.bind(this, 'free')}/>

                                </div>
                                <br/>
                                <label for="costs">
                                    <h4>Costs
                                    </h4>
                                    <div align="left">

                                        <div className="input-group " key="amount">
                                            <h5>Amount (in double)</h5>
                                            <input className="form-control" defaultValue={this.state.cost} type="double" ref="cost"/>
                                            <br/><br/>
                                            <h5>
                                                <b>
                                                    Note: PCF Console (Apps Manager) will not show the plan in Marketplace for an Organization,
                                                    <br/>
                                                    if cost is set to non-zero amount until the org quota is modified to allow access to non-basic (free) services
                                                </b>
                                            </h5>
                                        </div>

                                        <div className="input-group" key="currency">
                                            <label>
                                                <h5>
                                                    Currency</h5>
                                            </label>
                                            <input className="form-control" defaultValue={this.state.currency} type="text" ref="currency"/>
                                        </div>

                                        <div className="input-group" key="units">
                                            <h5>
                                                Units
                                            </h5>
                                            <input className="form-control" placeholder="select" defaultValue={this.state.units} list="units" ref="units"/>
                                            <datalist id="units">
                                                <option value="WEEKLY"/>
                                                <option value="BIWEEKLY"/>
                                                <option value="MONTHLY"/>
                                                <option value="YEARLY"/>
                                            </datalist>
                                        </div>
                                    </div>
                                </label>
                                <br/>

                                <TagsEditor tags={this.state.tagList} ref="tags"/>
                                <br/>
                                <CredentialsEditor planId={this.state.planId} ref="credentials"/>
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

    module.exports = EditPlan;

}());
