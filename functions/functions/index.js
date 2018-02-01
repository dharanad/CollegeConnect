'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore
    .document('/feed/notification/general/{notificationId}')
    .onCreate(event =>{
        const notification = event.data.data();
        const payload = {
            data : {
                type : 'notification',
                title : notification.title,
                message : notification.message,
                author : notification.author,
                uid : notification.uid,
                timestamp : notification.timestamp.toString()
            }
        };
        const topic = buildTopic('notification','general') + '';
        console.log(topic);
        return admin.messaging().sendToTopic(topic,payload)
            .then(res => {
                console.log('Successfully sent message : ' + JSON.stringify(res));
                console.log('Payload : ' + JSON.stringify(payload));
            }).catch(err =>{
                console.log('Error sending message : ' + JSON.stringify(err));
            });
    });

exports.sendAssignmentNotification = functions.firestore
    .document('/feed/assignment/{year}/{assignmentId}')
    .onCreate(event => {
        const path = event.params.year;
        const assignment = event.data.data();
        const payload = {
            data : {
                type : 'assignment',
                author : assignment.author,
                title : assignment.title,
                message : assignment.message,
                branch : assignment.branch,
                year : assignment.year,
                section : assignment.section,
                subject : assignment.subject,
                timestamp : assignment.timestamp.toString(),
                dueDate : assignment.dueDate.toString()
            }
        };
        const topic = buildTopic('assignment',path) + '-' + assignment.branch + '-' + assignment.section;
        console.log(topic);
        return admin.messaging().sendToTopic(topic,payload)
        .then(res => {
            console.log('Successfully sent message : ' + JSON.stringify(res))
            console.log('Payload : ' + JSON.stringify(payload));
        }).catch(err =>{
            console.log('Error sending message : ' + JSON.stringify(err));
        });
    });

exports.sendExamNotification = functions.firestore
    .document('/feed/exam/{year}/{examId}')
    .onCreate(event => {
        return;
    });

function buildTopic(type,path){
    return 'feed-'+type+'-'+path;
}