/** @jsx React.DOM */
var React = require('react');
var Router = require('react-router');
// PUI
var BasicPanelAlt = require('pui-react-panels').BasicPanelAlt;
var DefaultAltButton = require('pui-react-buttons').DefaultAltButton;
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;
var Plan = require('./plan.jsx');
var _ = require('lodash');

(function () {
    'use strict';

    var Plans = React.createClass({

        onAddPlan: function () {

            console.log("Got Add Plan event!...");
            var router = Router.create({});
            router.transitionTo('/addPlan/' + this.props.serviceEntry.id);
        },

        render: function () {
            console.log("Inside render plans: ", this.state, " and this contains: ", this);
            var plans = _.map(this.props.serviceEntry.plans, function (data) {
                console.log("Inside map of plan data: ", data);
                return React.createElement(Plan, data);
            });

            var PlanTitle = <div className="media">
                <div className="media-middle media-body">
                    <p className="type-dark-1 h4 mvn em-high">
                        {this.props.serviceEntry.name}
                        Service Plans
                    </p>
                </div>
                <div className="media-middle media-body txt-r">
                    <DefaultAltButton onClick={this.onAddPlan}>Add New Plan</DefaultAltButton>
                </div>
            </div>;

            return (
                <div className="container">
                    <BasicPanelAlt title={PlanTitle} className="mvxl" id="plan-panel">
                        {plans}
                    </BasicPanelAlt>
                </div>
            );
        }
    });

    module.exports = Plans;

}());
